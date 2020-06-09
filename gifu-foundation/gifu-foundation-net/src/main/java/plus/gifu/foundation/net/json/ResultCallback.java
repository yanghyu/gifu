package plus.gifu.foundation.net.json;

/**
 * 响应回调
 */
public interface ResultCallback {

    /**
     * 成功回调
     * @param responseData 响应数据
     */
    void success(String responseData);

    /**
     * 失败回调
     * @param responseData 响应数据
     */
    void fail(String responseData);

}
