#include "public.h"
//这个是RGB与HSL相互编码和译码文件
//HSL
extern double CCR_H;
extern double CCR_S;
extern double CCR_L;
//RBG
extern double CCR_R;
extern double CCR_B;
extern double CCR_G;


double max(double x,double y)
{
return (x > y ? x : y);
}


double min(double x,double y)
{
return (x > y ? y : x);
}

//RGB->HSL
void RGB2HSL()
{
    double R,G,B,Max,Min,del_R,del_G,del_B,del_Max;
    R = CCR_R / 255.0;       //Where RGB values = 0 - 255
    G = CCR_G / 255.0;
    B = CCR_B / 255.0;

    Min = min(R, min(G, B));    //Min. value of RGB
    Max = max(R, max(G, B));    //Max. value of RGB
    del_Max = Max - Min;        //Delta RGB value

    CCR_L = (Max + Min) / 2.0;

    if (del_Max == 0)           //This is a gray, no chroma...
    {
        CCR_H = 0;                  //HSL results = 0-1
        CCR_S = 0;
    }
    else                        //Chromatic data...
    {
        if (CCR_L < 0.5) CCR_S = del_Max / (Max + Min);
        else         CCR_S = del_Max / (2 - Max - Min);

        del_R = (((Max - R) / 6.0) + (del_Max / 2.0)) / del_Max;
        del_G = (((Max - G) / 6.0) + (del_Max / 2.0)) / del_Max;
        del_B = (((Max - B) / 6.0) + (del_Max / 2.0)) / del_Max;

        if      (R == Max) CCR_H = del_B - del_G;
        else if (G == Max) CCR_H= (1.0 / 3.0) + del_R - del_B;
        else if (B == Max) CCR_H= (2.0 / 3.0) + del_G - del_R;

        if (CCR_H < 0)  CCR_H += 1;
        if (CCR_H > 1)  CCR_H -= 1;
    }
}


//HSL->RGB:
double Hue2RGB(double v1, double v2, double vH)
{
    if (vH < 0) vH += 1;
    if (vH > 1) vH -= 1;
    if (6.0 * vH < 1) return v1 + (v2 - v1) * 6.0 * vH;
    if (2.0 * vH < 1) return v2;
    if (3.0 * vH < 2) return v1 + (v2 - v1) * ((2.0 / 3.0) - vH) * 6.0;
    return (v1);
}

void HSL2RGB(double H,double S,double L)
{
    double R,G,B;
    double var_1, var_2;
    if (S == 0)                       //HSL values = 0 - 1
    {
        R = L * 255.0;                   //RGB results = 0 - 255
        G = L * 255.0;
        B = L * 255.0;
    }
    else
    {
        if (L < 0.5) var_2 = L * (1 + S);
        else         var_2 = (L + S) - (S * L);

        var_1 = 2.0 * L - var_2;

        R = 255.0 * Hue2RGB(var_1, var_2, H + (1.0 / 3.0));
        G = 255.0 * Hue2RGB(var_1, var_2, H);
        B = 255.0 * Hue2RGB(var_1, var_2, H - (1.0 / 3.0));
    }
    CCR_R=R;
		CCR_B=B;
		CCR_G=G;
}



