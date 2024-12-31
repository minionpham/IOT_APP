package Api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryInterceptor implements Interceptor {
    private int maxRetryCount;

    public RetryInterceptor(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount; // Số lần thử lại tối đa
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        IOException lastException = null;
        Response response = null;

        for (int i = 0; i <= maxRetryCount; i++) {
            try {
                response = chain.proceed(request); // Gửi yêu cầu
                return response; // Nếu thành công, trả về phản hồi
            } catch (IOException e) {
                lastException = e; // Lưu lại lỗi
                if (i == maxRetryCount) {
                    throw lastException; // Nếu vượt quá số lần thử, ném lỗi
                }
            }
        }
        throw lastException; // Nếu không thành công sau các lần thử, ném lỗi
    }
}
