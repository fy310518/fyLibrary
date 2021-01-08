package com.fy.baselibrary.retrofit.webservice;

/**
 * description 定义 soap:Envelope 响应接口，用于获取 soap:Body 对象
 * Created by fangs on 2021/1/8 11:19.
 */
public interface IResponseEnvelope<P> {

    /**
     * 获取 soap:Body 对象
     * @return IResponseBody<P>
     */
    IResponseBody<P> getAssetResponseBody();
}


/**
 * description soap 响应模板实体类【参考 https://www.jianshu.com/p/6e4e53d8efe8?winzoom=1】
 * Created by fangs on 2021/1/7 17:38.
 */
//@Root(name = "soap:Envelope")
//@NamespaceList({
//        @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance/", prefix = "xsi"),
//        @Namespace(reference = "http://www.w3.org/2001/XMLSchema/", prefix = "xsd"),
//        @Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "soap")
//})
//public class ResponseEnvelope<P> implements IResponseEnvelope<P>{
//
//    @Element(name = "Body", required = false)
//    private IResponseBody<P> responseBody;
//
//    @Override
//    public IResponseBody<P> getAssetResponseBody() {
//        return responseBody;
//    }
//
//
//    @Root(name = "Body", strict = false)
//    public static class AssetResponseBody<P> implements IResponseBody<P> {
//        @Element(name = "AssetMaterialInfoResponse", required = false)
//        public P responseModel;
//
//        @Override
//        public P getResponseModel() {
//            return responseModel;
//        }
//    }
//
//    /*
//        泛型 P：所对应的 响应实体类 模板
//        @Root(name = "AssetMaterialInfoResponse")
//        public static class AssetResponseModel {
//            @Attribute(name = "xmlns", empty = "http://tempuri.org/", required = false)
//            public String nameSpace;
//            @Element(name = "AssetMaterialInfoResult")
//            public String result;
//        }
//    */
//}
