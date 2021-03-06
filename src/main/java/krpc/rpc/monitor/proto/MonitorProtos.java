// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: krpcmonitor.proto

package krpc.rpc.monitor.proto;

public final class MonitorProtos {
    private MonitorProtos() {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistry registry) {
        registerAllExtensions(
                (com.google.protobuf.ExtensionRegistryLite) registry);
    }

    static final com.google.protobuf.Descriptors.Descriptor
            internal_static_RpcStat_descriptor;
    static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_RpcStat_fieldAccessorTable;
    static final com.google.protobuf.Descriptors.Descriptor
            internal_static_ReportRpcStatReq_descriptor;
    static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_ReportRpcStatReq_fieldAccessorTable;
    static final com.google.protobuf.Descriptors.Descriptor
            internal_static_ReportRpcStatRes_descriptor;
    static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_ReportRpcStatRes_fieldAccessorTable;

    public static com.google.protobuf.Descriptors.FileDescriptor
    getDescriptor() {
        return descriptor;
    }

    private static com.google.protobuf.Descriptors.FileDescriptor
            descriptor;

    static {
        java.lang.String[] descriptorData = {
                "\n\021krpcmonitor.proto\032\rkrpcext.proto\"\213\001\n\007R" +
                        "pcStat\022\014\n\004type\030\001 \001(\005\022\014\n\004time\030\002 \001(\003\022\021\n\tse" +
                        "rviceId\030\003 \001(\005\022\r\n\005msgId\030\004 \001(\005\022\017\n\007success\030" +
                        "\005 \001(\005\022\016\n\006failed\030\006 \001(\005\022\017\n\007timeout\030\007 \001(\005\022\020" +
                        "\n\010timeUsed\030\010 \003(\005\"Y\n\020ReportRpcStatReq\022\021\n\t" +
                        "timestamp\030\001 \001(\003\022\014\n\004host\030\002 \001(\t\022\013\n\003app\030\003 \001" +
                        "(\t\022\027\n\005stats\030\004 \003(\0132\010.RpcStat\"#\n\020ReportRpc" +
                        "StatRes\022\017\n\007retCode\030\001 \001(\0052S\n\016MonitorServi" +
                        "ce\022;\n\rreportRpcStat\022\021.ReportRpcStatReq\032\021" +
                        ".ReportRpcStatRes\"\004\220\265\030\001\032\004\210\265\030\002B,\n\026krpc.rp" +
                        "c.monitor.protoB\rMonitorProtosP\001\210\001\001b\006pro" +
                        "to3"
        };
        com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
                new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
                    public com.google.protobuf.ExtensionRegistry assignDescriptors(
                            com.google.protobuf.Descriptors.FileDescriptor root) {
                        descriptor = root;
                        return null;
                    }
                };
        com.google.protobuf.Descriptors.FileDescriptor
                .internalBuildGeneratedFileFrom(descriptorData,
                        new com.google.protobuf.Descriptors.FileDescriptor[]{
                                krpc.KrpcExt.getDescriptor(),
                        }, assigner);
        internal_static_RpcStat_descriptor =
                getDescriptor().getMessageTypes().get(0);
        internal_static_RpcStat_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_RpcStat_descriptor,
                new java.lang.String[]{"Type", "Time", "ServiceId", "MsgId", "Success", "Failed", "Timeout", "TimeUsed",});
        internal_static_ReportRpcStatReq_descriptor =
                getDescriptor().getMessageTypes().get(1);
        internal_static_ReportRpcStatReq_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_ReportRpcStatReq_descriptor,
                new java.lang.String[]{"Timestamp", "Host", "App", "Stats",});
        internal_static_ReportRpcStatRes_descriptor =
                getDescriptor().getMessageTypes().get(2);
        internal_static_ReportRpcStatRes_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_ReportRpcStatRes_descriptor,
                new java.lang.String[]{"RetCode",});
        com.google.protobuf.ExtensionRegistry registry =
                com.google.protobuf.ExtensionRegistry.newInstance();
        registry.add(krpc.KrpcExt.msgId);
        registry.add(krpc.KrpcExt.serviceId);
        com.google.protobuf.Descriptors.FileDescriptor
                .internalUpdateFileDescriptor(descriptor, registry);
        krpc.KrpcExt.getDescriptor();
    }

    // @@protoc_insertion_point(outer_class_scope)
}
