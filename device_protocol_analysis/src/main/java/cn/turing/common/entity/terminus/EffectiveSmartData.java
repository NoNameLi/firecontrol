package cn.turing.common.entity.terminus;

import lombok.Data;

@Data
public class EffectiveSmartData {
    public String RES_CURRENT1;
    public String RES_CURRENT2;
    public String RES_CURRENT3;
    public String RES_CURRENT4;
    public String TEMP1;
    public String TEMP2;
    public String TEMP3;
    public String TEMP4;
    public String VOLTAGE_A;
    public String VOLTAGE_B;
    public String VOLTAGE_C;
    public String ELE_CURRENT_A;
    public String ELE_CURRENT_B;
    public String ELE_CURRENT_C;
    public String RES_CURRENT1_VALUE;
    public String RES_CURRENT2_VALUE;
    public String RES_CURRENT3_VALUE;
    public String RES_CURRENT4_VALUE;
    public String TEMP1_VALUE;
    public String TEMP2_VALUE;
    public String TEMP3_VALUE;
    public String TEMP4_VALUE;
    public String VOLTAGE_A_VALUE;
    public String VOLTAGE_B_VALUE;
    public String VOLTAGE_C_VALUE;
    public String ELE_CURRENT_A_VALUE;
    public String ELE_CURRENT_B_VALUE;
    public String ELE_CURRENT_C_VALUE;
    public String PA;
    public String PB;
    public String PC;
    public String COSQA;
    public String COSQB;
    public String COSQC;
    public String HZ_A;
    public String HZ_B;
    public String HZ_C;
    public String EPA;
    public String EPB;
    public String EPC;

    public EffectiveSmartData(String data) {
        RES_CURRENT1 = data.substring(0, 28);
        RES_CURRENT2 =data.substring(28, 56);
        RES_CURRENT3=data.substring(56, 84);
        RES_CURRENT4=data.substring(84, 112);
        TEMP1=data.substring(112, 140);
        TEMP2=data.substring(140, 168);
        TEMP3=data.substring(168, 196);
        TEMP4=data.substring(196, 224);
        VOLTAGE_A=data.substring(224, 252);
        VOLTAGE_B=data.substring(252, 280);
        VOLTAGE_C=data.substring(280, 308);
        ELE_CURRENT_A=data.substring(308, 336);
        ELE_CURRENT_B=data.substring(336, 364);
        ELE_CURRENT_C=data.substring(364, 392);


        RES_CURRENT1_VALUE=data.substring(392, 416);
        RES_CURRENT2_VALUE=data.substring(414+2, 438+2);
        RES_CURRENT3_VALUE=data.substring(438+2, 462+2);
        RES_CURRENT4_VALUE=data.substring(462+2, 486+2);
        TEMP1_VALUE=data.substring(486+2, 510+2);
        TEMP2_VALUE=data.substring(510+2, 534+2);
        TEMP3_VALUE=data.substring(534+2, 558+2);
        TEMP4_VALUE=data.substring(558+2, 582+2);
        VOLTAGE_A_VALUE=data.substring(582+2, 606+2);
        VOLTAGE_B_VALUE=data.substring(606+2, 630+2);
        VOLTAGE_C_VALUE=data.substring(630+2,654+2);
        ELE_CURRENT_A_VALUE=data.substring(654+2, 678+2);
        ELE_CURRENT_B_VALUE=data.substring(678+2, 702+2);
        ELE_CURRENT_C_VALUE=data.substring(702+2, 726+2);


        PA=data.substring(726+2, 754+2);
        PB=data.substring(754+2, 782+2);
        PC=data.substring(782+2, 810+2);

        COSQA=data.substring(810+2,834+2);
        COSQB=data.substring(834+2, 858+2);
        COSQC=data.substring(858+2, 882+2);
        HZ_A=data.substring(882+2, 906+2);
        HZ_B=data.substring(906+2, 930+2);
        HZ_C=data.substring(930+2, 954+2);

        EPA=data.substring(954+2, 982+2);
        EPB=data.substring(982+2, 1010+2);
        EPC=data.substring(1010+2, 1038+2);

    }
}
