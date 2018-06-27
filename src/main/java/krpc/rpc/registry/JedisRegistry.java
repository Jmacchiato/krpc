package krpc.rpc.registry;

import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import krpc.common.InitClose;
import krpc.common.Json;
import krpc.rpc.core.DynamicRouteConfig;
import krpc.rpc.core.DynamicRoutePlugin;
import krpc.rpc.core.Plugin;
import krpc.rpc.core.Registry;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class JedisRegistry  implements Registry,InitClose,DynamicRoutePlugin  {

	static Logger log = LoggerFactory.getLogger(JedisRegistry.class);

	String addrs;
	boolean enableRegist = true;
	boolean enableDiscover = true;
	
	int ttl = 90;
    int interval = 15;

	private JedisPool jedisPool;
	private JedisCluster jedisCluster;
	private boolean clusterMode = false;
	
    ConcurrentHashMap<String,String> versionCache = new ConcurrentHashMap<>();
    
    // set dynamicroutes.default.100.routes.json.version 1
    // set dynamicroutes.default.100.routes.json '{"serviceId":100,"disabled":false,"weights":[{"addr":"192.168.31.27","weight":50},{"addr":"192.168.31.28","weight":50}],"rules":[{"from":"host = 192.168.31.27","to":"host = 192.168.31.27","priority":2},{"from":"host = 192.168.31.28","to":"host = $host","priority":1}]}'
	
    public void init() {

		if(!clusterMode) {
			String[] ss = addrs.split(":");
			jedisPool = new JedisPool(ss[0],Integer.parseInt(ss[1]));
		} else {
			Set<HostAndPort> hosts = new HashSet<>();
			String[] ss =  addrs.split(",");
			for(String s : ss ) {
				String[] tt = s.split(":");
				hosts.add(new HostAndPort(tt[0],Integer.parseInt(tt[1])));
			}
			try {
				jedisCluster = new JedisCluster(hosts);
			} catch(Exception e) {
				throw new RuntimeException("cannot init jedis cluster",e);
			}
		}
    }	
    
	public void config(String paramsStr) {

		Map<String,String> params = Plugin.defaultSplitParams(paramsStr);
		
		addrs = params.get("addrs");

		String s = params.get("enableRegist");
		if( !isEmpty(s) ) enableRegist = Boolean.parseBoolean(s);	

		s = params.get("enableDiscover");
		if( !isEmpty(s) ) enableDiscover = Boolean.parseBoolean(s);	
		
		s = params.get("ttlSeconds");
		if( !isEmpty(s) ) ttl = Integer.parseInt(s);	
		
		s = params.get("intervalSeconds");
		if( !isEmpty(s) ) interval = Integer.parseInt(s);	
		
		s = params.get("clusterMode");
		if ( s != null && !s.isEmpty() )
			clusterMode = Boolean.parseBoolean(s);				
		
	}
    
    public void close() {
		if(!clusterMode) {
			jedisPool.close();
		} else {
			try {
				jedisCluster.close();
			} catch(Exception e) {
				log.error("close cluster exception, e="+e.getMessage());
			}
		}
    }    
    
 	boolean isEmpty(String s) {
 		return s == null || s.isEmpty();
 	}    
     
    public int getCheckIntervalSeconds() {
    	return interval;
    }
    
    public int getRefreshIntervalSeconds() {
    	return interval;
    }
	
    public DynamicRouteConfig getConfig(int serviceId,String serviceName,String group) {
    	 
    	String path = "dynamicroutes."+group+"."+serviceId+".routes.json";
    	String versionPath = path+".version";
    	
    	String key = serviceId + "." + group;
    	String oldVersion = versionCache.get(key);
    	String newVersion = null;
    	
		if(!clusterMode) {
	        Jedis jedis = null;  
	        try {  
	            jedis = jedisPool.getResource();  
	            newVersion = jedis.get(versionPath);
	            if( newVersion == null ) return null;
	        } catch (Exception e) {  
	        	log.error("cannot get routes json version for service "+serviceName+", exception="+e.getMessage());
	            return null;
	        } finally {  
	        	try {
	        		if( jedis != null )
	        			jedis.close();
	        	} catch(Exception e) {
	        	}
	        }  
		} else {
			try {  
				newVersion = jedisCluster.get(versionPath);
				if( newVersion == null ) return null;
			} catch (Exception e) {  
				log.error("cannot get routes json version for service "+serviceName+", exception="+e.getMessage());
	            return null;
	        }
		}
		
		if( oldVersion != null && newVersion != null && oldVersion.equals(newVersion) ) {
			return null; // no change
		}
		
		DynamicRouteConfig config = null;
		String json = null;
		
		if(!clusterMode) {
	        Jedis jedis = null;  
	        try {  
	            jedis = jedisPool.getResource();  
	            json = jedis.get(path);
	        } catch (Exception e) {  
	        	log.error("cannot get routes json for service "+serviceName+", exception="+e.getMessage());
	            return null;
	        } finally {  
	        	try {
	        		if( jedis != null )
	        			jedis.close();
	        	} catch(Exception e) {
	        	}
	        }  
		} else {
			try {  
				json = jedisCluster.get(path);
			} catch (Exception e) {  
				log.error("cannot get routes json for service "+serviceName+", exception="+e.getMessage());
	            return null;
	        }
		}
		
    	config = Json.toObject(json,DynamicRouteConfig.class);			
    	if( config == null ) {
    		log.error("invalid routes json for service "+serviceName+", json="+json);
    		return null;
    	}

		versionCache.put(key,newVersion);
		return config;

	}
	
	public void register(int serviceId,String serviceName,String group,String addr) {
		if( !enableRegist ) return;
		
		String instanceId = addr ;
		String path = "services."+group+"."+serviceId;
		
		long now = System.currentTimeMillis();
		HashMap<String,Object> meta = new HashMap<>();
		meta.put("addr", addr);
		meta.put("group", group);
		meta.put("serviceName", serviceName);
		meta.put("lastActive", now);
		String data = Json.toJson(meta);
		
		if(!clusterMode) {
	        Jedis jedis = null;  
	        try {  
	            jedis = jedisPool.getResource();  
	            jedis.hset(path,instanceId,data);
	        } catch (Exception e) {  
	        	log.error("cannot register service "+serviceName+", exception="+e.getMessage());
	            return;
	        } finally {  
	        	try {
	        		if( jedis != null )
	        			jedis.close();
	        	} catch(Exception e) {
	        	}
	        }  
		} else {
			try {  
	            jedisCluster.hset(path,instanceId,data);
			} catch (Exception e) {  
				log.error("cannot register service "+serviceName+", exception="+e.getMessage());
	            return;
	        }
		}

	}
	
	public void deregister(int serviceId,String serviceName,String group,String addr) {
		
		if( !enableRegist ) return;
		
		String instanceId = addr;
		String path = "services."+group+"."+serviceId;
		
		if(!clusterMode) {
	        Jedis jedis = null;  
	        try {  
	            jedis = jedisPool.getResource();  
	            jedis.hdel(path,instanceId);
	        } catch (Exception e) {  
	        	log.error("cannot deregister service "+serviceName+", exception="+e.getMessage());
	            return;
	        } finally {  
	        	try {
	        		if( jedis != null )
	        			jedis.close();
	        	} catch(Exception e) {
	        	}
	        }  
		} else {
			try {  
	            jedisCluster.hdel(path,instanceId);
			} catch (Exception e) {  
				log.error("cannot deregister service "+serviceName+", exception="+e.getMessage());
	            return;
	        }
		}

	}	
	
	public String discover(int serviceId,String serviceName,String group) {	
		
		if( !enableDiscover ) return null;
		
		String path = "services."+group+"."+serviceId;

		Map<String,String> v;
		
		if(!clusterMode) {
	        Jedis jedis = null;  
	        try {  
	            jedis = jedisPool.getResource();  
	            v = jedis.hgetAll(path);
	        } catch (Exception e) {  
	            log.error("cannot load key, key="+path);
	            return null;
	        } finally {  
	        	try {
	        		if( jedis != null )
	        			jedis.close();
	        	} catch(Exception e) {
	        	}
	        }  
		} else {
			try {  
	            v = jedisCluster.hgetAll(path);
			} catch (Exception e) {  
	            log.error("cannot load key, key="+path);
	            return null;
	        }
		}
		
		long now = System.currentTimeMillis();
		
		TreeSet<String> set = new TreeSet<>();
		for(Map.Entry<String,String> entry: v.entrySet()) {
			String key = entry.getKey();
			String json = entry.getValue();
			Map values = Json.toMap(json);
			long lastActive = Long.parseLong( values.get("lastActive").toString() ) ;
			if( now - lastActive < ttl * 1000 ) {
				set.add(key);
			}
		}
		
		StringBuilder b = new StringBuilder();
		for(String key: set) {
			if( b.length() > 0 ) b.append(",");
			b.append(key);
		}
		String s = b.toString();
		return s;
	}	
	
}
