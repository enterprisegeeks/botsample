package uls.hack.botsample;

import java.io.IOError;
import java.io.IOException;
import java.util.function.BiConsumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** コールバック定義が面倒なので */
public class LambdaCallback {
	
	public static <T> Callback<T> callback(BiConsumer<Call<T>, Response<T>> onResponse, 
			BiConsumer<Call<T>, Throwable> onFailure) {
		return new Callback<T>() {
			@Override
			public void onResponse(Call<T> call, Response<T> response) {
				onResponse.accept(call, response);
			}
			@Override
			public void onFailure(Call<T> call, Throwable t) {
				onFailure.accept(call, t);
			}
		};
	}

	public static <T> Callback<T> callback(BiConsumer<Call<T>, Response<T>> onResponse) {
		return callback(onResponse, (c, t) -> {});
	}
	
}
