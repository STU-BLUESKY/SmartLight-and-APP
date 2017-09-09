#ifndef _PCF8563_H_
#define _PCF8563_H_
#include "IIC.h"
#define ReadCode 0xa3
#define WriteCode 0xa2
void PCF8563_Init(void);
u8 PCF8563_ReaDAdress(u8 Adress);
void PCF8563_WriteAdress(u8 Adress, u8 DataTX);
//取回7个字节的数据:秒，分，时，天，星期，月份，年份。 //全局变量
extern u8 PCF8563_Time[7];
void PCF8563_ReadTimes(void);
//在CLKOUT上定时1S输出一个下降沿脉冲
void PCF8563_CLKOUT_1s(void);
#endif
