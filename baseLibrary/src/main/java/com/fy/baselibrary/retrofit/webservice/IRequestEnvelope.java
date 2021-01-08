package com.fy.baselibrary.retrofit.webservice;

/**
 * description 定义 soap:Envelope 接口 用于设置 soap:Body 对象
 * retrofit 使用 SimpleXmlConverterFactory 解析 xml 格式数据
 * Created by fangs on 2021/1/8 11:01.
 */
public interface IRequestEnvelope<R> {

    /**
     * 定义设置 soap:Body 对象方法
     */
    void setRequestHeadBody(IRequestHeadBody<R> headBody);

}

/**
 * 具体还有看 实际的接口情况
 * description soap 请求模板实体类【参考 https://www.jianshu.com/p/6e4e53d8efe8?winzoom=1】
 * Created by fangs on 2021/1/7 17:14.
 */
//@Root(name = "soap:Envelope")
//@NamespaceList({
//        @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
//        @Namespace(reference = "http://www.w3.org/2001/XMLSchema", prefix = "xsd"),
//        @Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "soap")
//})
//public class RequestEnvelope<R> implements IRequestEnvelope<R> {
//
//    @Element(name = "soap:Body", required = false)
//    private IRequestHeadBody<R> body;
//
//    @Override
//    public void setRequestHeadBody(IRequestHeadBody<R> body) {
//        this.body = body;
//    }
//
//
//    @Root(name = "soap:Body", strict = false)
//    public static class RequestHeadBody<R> implements IRequestHeadBody<R> {
//
//        @Element(name = "AssetMaterialInfo", required = false)
//        public R AssetMaterialInfo;
//
//        @Override
//        public void setRequestModel(R model) {
//            AssetMaterialInfo = model;
//        }
//    }
//
//
//    /*
//        泛型 R： 所对应的 请求实体类 模板
//        @Root(name = "AssetMaterialInfo", strict = false)
//        @Namespace(reference = "http://tempuri.org/")
//        public static class RequestModel {
//            @Element(name = "date", required = false)
//            public String date;
//            @Element(name = "page", required = false)
//            public int page;
//        }
//    */
//}
