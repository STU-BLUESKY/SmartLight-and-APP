#ifndef _public_H
#define _public_H

#include "stm32f10x.h"
double max(double x,double y);
double min(double x,double y);
void RGB2HSL();
double Hue2RGB(double v1, double v2, double vH);
void HSL2RGB(double H,double S,double L);


#endif 




