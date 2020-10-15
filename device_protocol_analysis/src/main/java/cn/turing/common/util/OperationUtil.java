package cn.turing.common.util;

/**
 * 计算消防栓温度值
 */
public class OperationUtil {
    public static void main(String [] args){
      //  System.out.println(xfsWenDu());
    }

    public static  Double xfsWenDu(double x){
        double T=0;

        if (x>=483.8463){
            T=-20;

        }else if(x>=363.6294 && x<483.8463){

            T=-4.17*x+0.125;
        }else if(x>=275.8701 && x<363.6294){

            T=-5.68*x+5.625;
        }else if(x>=211.1656 && x<275.8701){

            T=-0.078*x+11.48;
        }else  if(x>=163.0086 && x<211.1656){

            T=-0.10*x+16.98;
        }else if(x>=126.8456 && x<163.0086){

            T=-0.14*x+22.03;
        }else if(x>=99.4589 && x<126.8456) {

            T=-0.19*x+28.33;
        }else if(x>=78.5505 && x<99.4589){

            T=-0.24*x+33.57;
        }else if(x>=62.4653 && x<78.5505){

            T=-0.3125*x+39.375;
        }else if(x>=50 && x<62.4653){

            T=-0.42*x+45.83;
        }else if(x>=40.2726 && x<50){

            T=-0.5*x+50;
        }else if(x>=32.6312 && x<40.2726){

            T=-0.625*x+55;
        }else if(x>=26.5903 && x<32.6312){

            T=-0.83*x+61.67;
        }else if(x>=21.7857 && x<26.5903){

            T=-0.1*x+66;
        }else if(x>=17.9421 && x<21.7857){

            T=-1.25*x+71.25;
        }else if(x>=14.8502 && x<17.9421){

            T=-1.67*x+78.33;
        }else if(x>=12.3498 && x<14.8502){

            T=-2.5*x+90;
        }else if(x>=10.3174 && x<12.3498){

            T=-2.5*x+90;
        }else if(x>=8.6573 && x<10.3174){

            T=-2.5*x+90;
        }else if(x>=7.295 && x<8.6573){

            T=-5*x+110;
        }else if(x>=6.1719 && x<7.295){

            T=-5*x+110;
        }else if(x>=5.242 && x<6.1719){

            T=-5*x+110;
        }else if(x<5.242){
            T=85;
        }


        return T;
    }
}
