# Retum Scanner

Retum Scanner is a card scanning library for Android. Default Implementation includes **CNIC Scanner**, which detects:
* CNIC Number
* Date of Birth 
* Date of Expiry 
* Date of Issue. 

Retum also allow you to write **Custom Predictors** which can predict the data of your **Custom Card**.


## Demo

![Demo](https://media.giphy.com/media/bcK86vcxE32k8qL5Mm/giphy.gif)


## Setup

1. Add Firebase to your project. Follow the [link](https://firebase.google.com/docs/android/setup) here.


2. Add it in your root build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  ```
  
3. Add the dependency
```
	dependencies {
	        implementation 'com.github.rehmanmuradali:retum.scanner:v1.0.2'
	}
 ```
 
 
 ## How to use Retum?
 ```
 buttonID.setOnClickListener(view -> 
              new Retum()
                    .withActivity(activity)
                    .setPredictor(new CnicPredictor())
                    .scan(new Retum.RetumCallback() {
                        @Override
                        public void onScanSuccess(AbstractBaseModel abstractBaseModel) {
                        
                        }

                        @Override
                        public void onScanFailure(String s) {

                        }
                      }
                    )
                );
 ```
 
 **Retrieve Predicted Data by Cnic Predictor:** 
 
 ```
 @Override
public void onScanSuccess(AbstractBaseModel abstractBaseModel) {
   CnicModel cnicModel = (CnicModel) abstractBaseModel;
   String filePath = cnicModel.getFilePath();
   String message = "cnic number: " + cnicModel.getCnicNumber() +
                "\ndate of issue: " + cnicModel.getCnicIssueDate() +
                "\ndate of expiry: " + cnicModel.getCnicExpiryDate() +
                "\ndate of birth: " + cnicModel.getDateOfBirth();                     
}
```

## Make Your Custom Predictor? 
**For example Credit Card**


**1) Make your CreditCardModel extends AbstractBaseModel:**
```
public class CreditCardModel extends AbstractBaseModel<CreditCardModel> {
    
    /**
       The current model is the newest one predicted. The b2 model is 
       the previous one predicted with max score. If you wants to merge
       two models then write your logic here, else return this
    **/
    
    @Override
    public CreditCardModel mergeModel(CreditCardModel b2) {
        return this;
    }

    
    /** 
       The function is used to compare two models that which one is 
       predicted more accurately, based on their scores. Returns the 
       value > 0 if newest model is better, value < 0 if previous 
       model is better, else return 0; 
    **/
    
    @Override
    public int compareTo(CreditCardModel b2) {
        return Integer.compare(getScore(), b2.getScore());
    }

    
    /**
       The function is used to get the score of particular model predicted 
       from a particular frame. Write your logic to calculate score of 
       particular model. 
      
    **/
    
    @Override
    public int getScore() {
        return 0;
    }

    /**
        The function is used to get the max score of a model so that program 
        know that the prediction is done and returns.
    **/

    @Override
    public int getMaxScore() {
        return -1;
    }

    /**
        The function is used to get the minimum score of a model so that program 
        can know that minimum fields are deteced and return a success callback, 
        else return a failure callback after Timeout( which is 10 seconds).
    **/
    
    @Override
    public int getMinScore() {
        return -1;
    }
}
```

**2) Make your Predictor implements BasePredictor**
```
public class CreditCardPredictor implements BasePredictor<CreditCardModel>
{
    @Override
    public CreditCardModel process(List<String> dataList, List<Element> elementList) {
       
        //   1) Do relevant prediction with the provided text ( dataList: converted from firebase OCR-On Device model)
        //   2) Set the predicted data to your custom model and return the model.
        CreditCardModel creditCardModel = predictData(dataList,elementList);
        
        return creditCardModel;
    }
}

```
## LICENSE
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

## Author
**Rehman Murad Ali**

 
