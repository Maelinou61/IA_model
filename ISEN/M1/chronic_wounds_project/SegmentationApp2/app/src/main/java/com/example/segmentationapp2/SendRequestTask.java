package com.example.segmentationapp2;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendRequestTask extends AsyncTask<Void, Void, int[][]>{

    private Bitmap image;
    private PredictionListener listener;

    public SendRequestTask(Bitmap image, PredictionListener listener){
        this.image = image;
        this.listener = listener;
    }

    @Override
    protected int[][] doInBackground(Void... params){
        try{
            // Convert image in base64
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            //Build HTTP request
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60000, TimeUnit.MILLISECONDS)
                    .build();

            MediaType mediaType = MediaType.parse("image/jpeg");

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", "image.jpg", RequestBody.create(mediaType, byteArray))
                    .build();

            Request request = new Request.Builder()
                    .url("https://flask-segmentation-app-4c96ce0b30c3.herokuapp.com/predict")
                    .post(requestBody)
                    .build();

            // Envoyer la requête et traiter la réponse
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String predictionResponse = response.body().string();
                JSONObject jsonObject = new JSONObject(predictionResponse);
                JSONArray predictionArray = jsonObject.getJSONArray("result");

                // Convertir la prédiction en tableau de flottants
                int[][] prediction = new int[320][320];
                for (int i = 0; i < 320; i++) {
                    JSONArray row = predictionArray.getJSONArray(i);
                    for (int j = 0; j < 320; j++) {
                        prediction[i][j] = row.getInt(j);
                    }
                }
                return prediction;
            }
            else{
                return null;
            }

        } catch (IOException | JSONException ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(int[][] prediction){
        if (listener !=null && prediction != null){
            listener.onPredictionReceived(prediction);
        }
        else {
            Log.e("SendRequestTask", "Failed to receive prediction");
        }
    }
}
