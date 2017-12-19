
#include <jni.h>
#include <string>
#include <opencv2/core.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>

using namespace std;
using namespace cv;

extern "C"{

void JNICALL Java_pae_iot_processingcpp_CameraActivity_lightProcessing(JNIEnv *env, jobject instance,
                                                     jlong matAddrGray,
                                                     jlong thresholdAddr) {
    Mat &mGr = *(Mat *) matAddrGray;
    Mat &threshold_v = *(Mat *) thresholdAddr;

    Mat col_mean= Mat::zeros(480,1, CV_64FC1);

    //CROP IMAGE (COLSxROWS)
    Point topLeft = cv::Point(420,0);
    Point bottomRight = cv::Point(640,480);

    Rect R(topLeft,bottomRight); //Create a rect
    Mat ROI = mGr(R); //Crop the region of interest using above rect

    //CALCULATE MEAN IN VECTOR
    reduce(ROI,col_mean, 1, CV_REDUCE_AVG,-1);
    Mat mean_vec;
    col_mean.convertTo(mean_vec,CV_64FC1);

    //POLYNOMIAL FITTING
    int order = 3;
    Mat src_x= Mat::zeros(480,1, CV_64FC1);
    Mat out_pow= Mat::zeros(480,1, CV_64FC1);
    Mat beta_coefs = Mat::zeros(order+1,1,CV_64FC1);
    Mat y = Mat::zeros(480,1,CV_64FC1);
    Mat inve = Mat::zeros(order+1,480,CV_64FC1);
    Mat V = Mat::ones(480,order+1, CV_64FC1);

    Mat substract_vec = Mat::zeros(480,1,CV_64FC1);
    Mat threshold_vec = Mat::zeros(480,1,CV_64FC1);
    Mat binary_vec;

    //Create 0,1,2,3,4,5...N-1
    for(int i=0;i<=480;i++){
        src_x.at<double>(i)=i+1;
    }

    for(int j=0; j<order+1; j++){
        pow(src_x, j, out_pow); //Fill row with src_x^i
        out_pow.copyTo(V.col(j));
    }

    invert(V,inve,DECOMP_SVD);
    beta_coefs = inve * mean_vec;
    y=V*beta_coefs;

    //THRESHOLD
    substract_vec = mean_vec-y;
    threshold(substract_vec,threshold_vec,1,1,THRESH_BINARY);

    //BINARYZE
    threshold_vec.convertTo(binary_vec,CV_16U);
    threshold_vec.copyTo(threshold_v);

    }

}
