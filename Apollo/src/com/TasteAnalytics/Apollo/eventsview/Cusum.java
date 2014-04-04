/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.Apollo.eventsview;

/**
 *
 * @author Wenwen
 */
public class Cusum {
    
    public static float[][] cusumProcess(float[] time_series, float alpha){
        /*
         * Sk = MAX(0, X(k) - (r + K) + Sum(k-1))  K=sd/2, r = mean 
         * 
         * 
         * @param time_series the timeseries
         * @param r - reference value, mean
         * @param k - slack sd*a
         * 
         * X(k) represent an absolute value.
         * 
         * r Mean for X(k)
         * 
         * @return
         *  result[i][0]: upper control
         *  result[i][1]: if event
         *
         * 
         */
        int n = time_series.length;
        float[][] result = new float[n][2];
        
        float r = mean(time_series, 0, n);
        float sd = standard_deviation(time_series, 0, n);
        float k = (float) (alpha*sd);
        float upperCusum = 4*sd;
        float lowerCusum = -4*sd;
        
        float tmpS = 0;
        float count = 1;
        for(int i=0; i<n; i++){
            tmpS = Math.max(0, time_series[i] - r - k + tmpS);
            result[i][0] = tmpS;
            
            if(i>0){
                if(result[i][0] > 0){
                    //if(time_series[i] > 15){
                       if(result[i-1][0] > 0){
                        count = count+1;
                        result[i][1] = count;
                    }else{
                        result[i][1] = count;
                    } 
//                    }else{
//                        result[i-1][1] = count;
//                        result[i][1] = 0;
//                    }
                    
                }else{
                    result[i][1] = 0;
                    if(result[i-1][0] > 0){
                        count = 1;
                    }
                }
            }else{
                if(result[i][0] > 0){
                    result[i][1] = 1;
                }else{
                    result[i][1] = 0;
                }
            }
        }
        
        return result;
    }
    
    // alarm eligibility
    public static float alarmEligibility(float count_value, float norm_value, float norm_forecast){
        return (((count_value > 10) || ((count_value > (float) 5.0) && (norm_value > (float) 3.0*norm_forecast))) ? (float) 1.0 : (float) 0.0);
    }
    
    
    // mean
    public static float mean(float[] doubArray, int startInd, int endInd) {
        if (startInd < 0)
            startInd = 0;
        float meanOut = (float) 0.0;
        for (int i = startInd; i < endInd; i++)
            meanOut += doubArray[i];
        meanOut /= (endInd - startInd + 1);
        return meanOut;
    }

    // standard deviation        
    public static float standard_deviation(float[] doubArray, int startInd,
            int endInd) {
        if (startInd < 0)
            startInd = 0;
        float stdOut = (float) 0.0;
        float themean = mean(doubArray, startInd, endInd);
        for (int i = startInd; i < endInd; i++)
            stdOut += (doubArray[i] - themean) * (doubArray[i] - themean);
        stdOut /= (endInd - startInd);
        stdOut = (float) Math.sqrt(stdOut);
        return stdOut;
    }
}
