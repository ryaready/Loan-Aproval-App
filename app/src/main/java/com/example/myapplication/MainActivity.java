package com.example.myapplication;
import com.example.myapplication.R;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private EditText nameEditText, genderEditText, marriedEditText, dependentsEditText,
            educationEditText, selfEmployedEditText, applicantIncomeEditText,
            coapplicantIncomeEditText, loanAmountEditText, loanAmountTermEditText,
            creditHistoryEditText, propertyAreaEditText;
    private ApiService apiService;
    private TextView predictionResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize EditTexts
        genderEditText = findViewById(R.id.gender);
        marriedEditText = findViewById(R.id.married);
        dependentsEditText = findViewById(R.id.dependents);
        educationEditText = findViewById(R.id.education);
        selfEmployedEditText = findViewById(R.id.self_employed);
        applicantIncomeEditText = findViewById(R.id.applicant_income);
        coapplicantIncomeEditText = findViewById(R.id.coapplicant_income);
        loanAmountEditText = findViewById(R.id.loan_amount);
        loanAmountTermEditText = findViewById(R.id.loan_amount_term);
        creditHistoryEditText = findViewById(R.id.credit_history);
        propertyAreaEditText = findViewById(R.id.property_area);

        predictionResultTextView = findViewById(R.id.prediction_result);

        Button submitButton = findViewById(R.id.submit_button);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.13.151:5000")  // Replace with your server IP
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gender = genderEditText.getText().toString();
                String married = marriedEditText.getText().toString();
                String dependents = dependentsEditText.getText().toString();
                String education = educationEditText.getText().toString();
                String selfEmployed = selfEmployedEditText.getText().toString();
                String applicantIncome = applicantIncomeEditText.getText().toString();
                String coapplicantIncome = coapplicantIncomeEditText.getText().toString();
                String loanAmount = loanAmountEditText.getText().toString();
                String loanAmountTerm = loanAmountTermEditText.getText().toString();
                String creditHistory = creditHistoryEditText.getText().toString();
                String propertyArea = propertyAreaEditText.getText().toString();

                // Call your API to get predictions using these input values
                sendLoanApprovalRequest(gender, married, dependents, education,
                        selfEmployed, applicantIncome, coapplicantIncome, loanAmount,
                        loanAmountTerm, creditHistory, propertyArea);
            }
        });
    }

    private void sendLoanApprovalRequest(String gender, String married, String dependents,
                                         String education, String selfEmployed, String applicantIncome,
                                         String coapplicantIncome, String loanAmount, String loanAmountTerm,
                                         String creditHistory, String propertyArea) {

        // Convert input features to float array
        float[] features = new float[11];
        features[0] = Float.parseFloat(applicantIncome); // Example: Input feature mapping
        features[1] = Float.parseFloat(coapplicantIncome);
        features[2] = Float.parseFloat(loanAmount);
        features[3] = Float.parseFloat(loanAmountTerm);
        features[4] = Float.parseFloat(creditHistory);
        features[5] = married.equalsIgnoreCase("Yes") ? 1 : 0; // Convert Yes/No to binary
        features[6] = selfEmployed.equalsIgnoreCase("Yes") ? 1 : 0;
        features[7] = Float.parseFloat(dependents);
        features[8] = gender.equalsIgnoreCase("Male") ? 1 : 0; // Convert Male/Female to binary
        features[9] = education.equalsIgnoreCase("Graduate") ? 1 : 0; // Assuming binary for simplification
        features[10] = getPropertyAreaValue(propertyArea); // Custom function to convert property area

        // Create the request object
        InputData request = new InputData(features);

        // Make the API call
        apiService.getPrediction(request).enqueue(new Callback<PredictionResponse>() {
            @Override
            public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String prediction = response.body().getPrediction();
                    // Update the prediction result TextView
                    predictionResultTextView.setText("Prediction: " + prediction);
                } else {
                    predictionResultTextView.setText("Failed to get prediction");
                }
            }

            @Override
            public void onFailure(Call<PredictionResponse> call, Throwable t) {
                predictionResultTextView.setText("Error: " + t.getMessage());
            }
        });
    }
    private int getPropertyAreaValue(String propertyArea) {
        // Custom logic to convert property area to an integer value
        switch (propertyArea) {
            case "Urban":
                return 1;
            case "Semiurban":
                return 2;
            case "Rural":
                return 3;
            default:
                return 0; // Default case
        }
    }

}
