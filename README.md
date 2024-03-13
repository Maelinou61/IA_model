IA ALGORITHM TO DETECT AND SEGMENT CHRONIC WOUNDS IN ORDER TO CALCULATE THEIR PERIMETER AND AREA.

Methodology and Description of the project: 
The goal of the project is to calculate automatically the area and the perimeter of a chronic wound thanks to IA. This project should be able to be used in connected glasses. 

Methodology: In order to realize this project, I used the article of Brayan Monroy : https://pubmed.ncbi.nlm.nih.gov/37633087/. To explain, a detection and segmentation process will take place in order to create a mask of the chronic wound. Moreover, a calibration panel with known dimension (radius = 1.35cm, side length = 3.15cm) should be place next to the wound. It will allow us to convert pixel into real cm. Consequently we will be able to calculate the real area and real perimeter of the chronic wound.

Two chronic wounds databases were used. The first one come from Brayan Monroy article. The second one come from this article : https://www.sciencedirect.com/science/article/pii/S0895611120301397. This is the only two databases that I found on the Internet and I have 328 images in my database. To increase this number of images and improve metrics of my models, I used transfer learning and data augmentation. 


To realize this project, I made several parts.

Detection part:
First, a wound detection algorithm was created. First, I have a yolov4 CNN by creating a Darknet model. You can find the code for training and testing in the Yolov4_2DB folder. This code and weights were created using AlexeyAB’s work whose Darknet repository you can find at the following link: https://github.com/AlexeyAB/darknet. You can not fin darknet model in this repository because the files were too big (more than 200MB)
However, the Darknet model created was too complex to be used in the android app (see segmentation_app2 repository). Indeed, after converting to tflite, the model crashed the application.

Therefore, I decided to create another detection model that you can find in the flite_detection folder. To achieve this, I based myself on the code presented in this youtube video (link to the code in the description): https://www.youtube.com/watch?v=XZ7FYAMCc4M. My repository has two models .tflite. One to detect chronic wounds and the calibration panel and the second to detect only chronic wounds. This last model was created in order to test an improvement of the detection of chronic wounds if the model was only trained on it. This only increased the mAP by 1% so I decided to keep the model being able to detect both forms of interest. 
This model is able to be used directly in an android app.

Segmentation part:
Once this detection step has been completed, it is time to move on to the chronic wounds segmentation step. For this, the results of the predictions made will serve us because we obtain the coordinates of the regions of interest (see file my_predictions.txt). This allows us to crop chronic wounds and put them directly into the input of our segmentation model. 

For segmentation,  a U-Net model was used as it is very efficient for medical data segmentation. You can find the code used in the Segmentation_U-Net_2DB folder. There is no segmentation model available on my github because the file is heavy (more than 60MB). However, you can create one with the my_predictions file and the images folder using the code . ipynb

I was not able to use this segmentation model reated directly into my app. Consequently, I put it on a server by creating a flask app (see corresponding repository). I called this flask app from android studio to recover the result and create the segmentation mask.


Circles detection:
Finally, the file find_circles allows to detect the circle inside the calibration panel. To optimize the detection, I cropped the calibration panel. 


To summarize, I created three models : two detection models : Yolov4 (not used) and tflite. It allows me to detect calibration panel and a chronic wound on an image. Thanks to the result, I can cropped this two region of interest. The chronic wounds cropped is an input for the U-Net and it gives me a mask as an input.
The calibration cropped is used to detect the circle inside it.
Thanks to this work, I was able to create an android app which was able to detect, segment and calculate area and perimeter of a chronic wound. 
