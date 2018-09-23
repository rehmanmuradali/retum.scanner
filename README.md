# Retum Scanner

Retum Scanner is a card scanning library for Android. Default Implementation includes **CNIC Scanner**, which detects:
* CNIC Number,
* Date of Birth 
* Date of Expiry 
* Date of Issue. 

Retum also allows to write **Custom Predictors** which can predict the data of your **Custom Card**.



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
 
 
 ## How to Use Retum?
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
        know that the prediction is done and return.
    **/

    @Override
    public int getMaxScore() {
        return -1;
    }

    /**
        The function is used to get the min score of a model so that program 
        can know that minimum fields are deteced and return a success callback, 
        else return a failure callback after Time0ut( Which is 10 seconds).
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
       
        //   1) Do relevant predictor with the provided text ( converted from firebase OCR-On Device model)
        //   2) Set the predicted data to your custom model and returm the model.
        CreditCardModel creditCardModel = predictData(dataList,elementList);
        
        return creditCardModel;
    }
}

```
 
## Author
**Rehman Murad Ali**

 
