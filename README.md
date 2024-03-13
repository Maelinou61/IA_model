IA ALGORITHM TO DETECT AND SEGMENT CHRONIC WOUNDS IN ORDER TO CALCULATE THEIR PERIMETER AND AREA.

Methodology and Description of the project: 
The goal of the project is to calculate automatically the area and the perimeter of a chronic wound thanks to IA. This project should be able to be used in connected glasses. 

Methodology: In order to realize this project, I used the article of Brayan Monroy : https://pubmed.ncbi.nlm.nih.gov/37633087/. To explain, a detection and segmentation process will take place in order to create a mask of the chronic wound. Moreover, a calibration panel with known dimension (radius = 1.35cm, side length = 3.15cm) should be place next to the wound. It will allow us to convert pixel into real cm. Consequently we will be able to calculate the real area and real perimeter of the chronic wound.

Two chronic wounds databases were used. The first one come from Brayan Monroy article. The second one come from this article : https://www.sciencedirect.com/science/article/pii/S0895611120301397. This is the only two databases that I found on the Internet and I have 328 images in my database. To increase this number of images and improve metrics of my models, I used transfer learning and data augmentation. 


To realize this project, I made several parts.

Detection part:
First, a wound detection algorithm was created. First, I have a yolov4 CNN by creating a Darknet model. You can find the code for training and testing in the Yolov4_2DB folder. This code and weights were created using AlexeyAB’s work whose Darknet repository you can find at the following link: https://github.com/AlexeyAB/darknet. 20% of the images was considered as a test and the rest of the images was used to train the model. You can not fin darknet model in this repository because the files were too big (more than 200MB)
However, the Darknet model created was too complex to be used in the android app (see segmentation_app2 repository). Indeed, after converting to tflite, the model crashed the application.
Therefore, I decided to create another detection model that you can find in the flite_detection folder. To achieve this, I based myself on the code presented in this youtube video (link to the code in the description): https://www.youtube.com/watch?v=XZ7FYAMCc4M. My repository has two models .tflite. One to detect chronic wounds and the calibration panel and the second to detect only chronic wounds. This last model was created in order to test an improvement of the detection of chronic wounds if the model was only trained on it. This only increased the mAP by 1% so I decided to keep the model being able to detect both forms of interest. In the two cases, 80% of the data was used to train my model, 10% of validation and 10% of test.
This model is able to be used directly in an android app.

Segmentation part:
Once this detection step has been completed, it is time to move on to the chronic wounds segmentation step. For this, the results of the predictions made will serve us because we obtain the coordinates of the regions of interest (see file my_predictions.txt). This allows us to crop chronic wounds and put them directly into the input of our segmentation model. The database was separated into 75% of train and 25% of test
For segmentation,  a U-Net model was used as it is very efficient for medical data segmentation. You can find the code used in the Segmentation_U-Net_2DB folder. There is no segmentation model available on my github because the file is heavy (more than 60MB). However, you can create one with the my_predictions file and the images folder using the code .ipynb. 
I was not able to use this segmentation model reated directly into my app. Consequently, I put it on a server by creating a flask app (see corresponding repository). I called this flask app from android studio to recover the result and create the segmentation mask.

Circles detection:
Finally, the file find_circles allows to detect the circle inside the calibration panel thanks to cv2.HoughCirles function. To optimize the detection, I cropped the calibration panel.

To summarize, I created three models : two detection models : Yolov4 (not used) and tflite. It allows me to detect calibration panel and a chronic wound on an image. Thanks to the result, I can cropped this two region of interest. The chronic wounds cropped is an input for the U-Net and it gives me a mask as an input.
The calibration cropped is used to detect the circle inside it.
Thanks to this work, I was able to create an android app which was able to detect, segment and calculate area and perimeter of a chronic wound. 


Results and metrics:
Detection model shows very good result to detect the calibration panel (mAP = 90.79%). However, the detection of chronic wounds is less performant. For the model which detect calibration panel and chronic wounds, mAP = 38.46%. For the second model which detect only chronic wounds, mAP = 39.42%. These results can be explained by the little number of chronic wounds in the database and also by the many different forms of chronic wounds.
![image](https://github.com/Maelinou61/IA_model/assets/157109478/a94d64c4-ca3b-4cbf-b97f-3c2a30893c43)
![image](https://github.com/Maelinou61/IA_model/assets/157109478/8357410b-e106-488a-b8b1-b984d3f5ef91)


Segmentation model shows very good result. I made ten runs to train ten different U-Net model with different training database. On 10 runs, the average metrics are: loss=0.08466, dice_coef=0.8339, precision=0.9369, recall=0.8566, accuracy=0.9528. 
![image](https://github.com/Maelinou61/IA_model/assets/157109478/54190bcc-5c13-4a35-a29a-5636b4e1eac2)

About the circles detection, I found satisfying parameters for cv2.HoughCircles function. It detects approximately 40% of the circles inside cropped calibration panel. This percentage can be explained by the fact that sometimes, images are blurred or the calibration is at the extremity of the image. However, when a circles is detected, most of the time, it corresponds to the real circle inside the calibration panel.
![image](https://github.com/Maelinou61/IA_model/assets/157109478/cf641e1e-66ab-4cb5-ad27-0dedecf6a0a4)



FAQ:
Please, if you have any question about the project, don't hesitate. Moreover, as there is a lot of files, I could forgot some of them so notice me if something doesn't work.

