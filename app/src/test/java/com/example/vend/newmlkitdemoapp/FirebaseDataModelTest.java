package com.example.vend.newmlkitdemoapp;

import com.example.vend.newmlkitdemoapp.client.cnic.client.model.CnicModel;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class FirebaseDataModelTest {

    @Test
    public void compareCnicDataModel_PassingValidValues_ReturnsValidInteger() {
        CnicModel cnicDataModelX = new CnicModel();
        cnicDataModelX.setDateOfBirth("23 02 1996");
        cnicDataModelX.setCnicIssueDate("20 03 2022");
        cnicDataModelX.setCnicNumber("4210179067351");


        CnicModel cnicDataModelY = new CnicModel();
        cnicDataModelY.setDateOfBirth("23 02 1996");
        cnicDataModelY.setCnicIssueDate("20 03 2022");


        CnicModel cnicDataModelZ = new CnicModel();
        cnicDataModelZ.setDateOfBirth("23 02 1996");
        cnicDataModelZ.setCnicIssueDate("20 03 2022");




        assertEquals(1, cnicDataModelX.compareTo(cnicDataModelY));
        assertEquals(-1, cnicDataModelY.compareTo(cnicDataModelX));
        assertEquals(0, cnicDataModelY.compareTo(cnicDataModelZ));

    }

    @Test
    public void mergeFirebaseDataModel_PassingValid_Values_ReturnsMergedModel() {

        CnicModel cnicDataModelX = new CnicModel();
        cnicDataModelX.setDateOfBirth("23 02 1996");
        cnicDataModelX.setCnicIssueDate("20 03 2018");
        cnicDataModelX.setCnicExpiryDate("20 03 2022");


        CnicModel cnicDataModelY = new CnicModel();
        cnicDataModelY.setCnicNumber("4210179067351");


        CnicModel cnicDataModelA = new CnicModel();
        cnicDataModelA.setDateOfBirth("23 02 1996");

        CnicModel cnicDataModelB = new CnicModel();
        cnicDataModelB.setDateOfBirth("20 03 2018");
        cnicDataModelB.setCnicIssueDate("20 03 2018");
        cnicDataModelB.setCnicExpiryDate("20 03 2022");


        CnicModel cnicDataModelZ = new CnicModel();
        cnicDataModelZ.setDateOfBirth("23 02 1996");
        cnicDataModelZ.setCnicIssueDate("20 03 2018");
        cnicDataModelZ.setCnicExpiryDate("20 03 2022");
        cnicDataModelZ.setCnicNumber("4210179067351");


        assertEquals("23 02 1996",
                cnicDataModelA.mergeModel(cnicDataModelB)
                        .getDateOfBirth()
        );

        assertEquals("4210179067351",
                cnicDataModelB.mergeModel(cnicDataModelZ)
                        .getCnicNumber()
        );



    }

}
