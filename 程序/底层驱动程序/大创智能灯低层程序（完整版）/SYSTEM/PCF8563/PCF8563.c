#include "PCF8563.h"
//全局变量,程序初始的值就是要初始化的时间，
//用途：1：初始化时间。2：读取返回时间
// 秒，分，时，天，星期，月份，年份。
extern u8 PCF8563_Time[7];
void PCF8563_Init(void)
{
	IIC_Init();
	//十进制码转换成BCD码
	PCF8563_Time[0] = ((PCF8563_Time[0]/10) << 4) | (PCF8563_Time[0]%10); 
	PCF8563_Time[1] = ((PCF8563_Time[1]/10) << 4) | (PCF8563_Time[1]%10);
	PCF8563_Time[2] = ((PCF8563_Time[2]/10) << 4) | (PCF8563_Time[2]%10); 
	PCF8563_Time[3] = ((PCF8563_Time[3]/10) << 4) | (PCF8563_Time[3]%10); // PCF8563_Time[4] = ((PCF8563_Time[4]/10 << 4)) | (PCF8563_Time[4]%10); 期不用转换
	PCF8563_Time[5] = ((PCF8563_Time[5]/10 << 4)) | (PCF8563_Time[5]%10); 
	PCF8563_Time[6] = ((PCF8563_Time[6]/10 << 4)) | (PCF8563_Time[6]%10); PCF8563_CLKOUT_1s();
	PCF8563_WriteAdress(0x00, 0x20); //禁止RTC source clock
	//初始化PCF8563的时间
	PCF8563_WriteAdress(0x02, PCF8563_Time[0]);
	PCF8563_WriteAdress(0x03, PCF8563_Time[1]);
	PCF8563_WriteAdress(0x04, PCF8563_Time[2]);
	PCF8563_WriteAdress(0x05, PCF8563_Time[3]);
	PCF8563_WriteAdress(0x06, PCF8563_Time[4]);
	PCF8563_WriteAdress(0x07, PCF8563_Time[5]);
	PCF8563_WriteAdress(0x08, PCF8563_Time[6]);
	PCF8563_WriteAdress(0x00, 0x00); //Enable RTC sorce clock
}
u8 PCF8563_ReaDAdress(u8 Adress)
{
	u8 ReadData;
	IIC_Start();
	IIC_WriteByte(0xa2);
	IIC_WaitAck();
	IIC_WriteByte(Adress);
	IIC_WaitAck();
	IIC_Start();
	IIC_WriteByte(0xa3);
	IIC_WaitAck();
	ReadData = IIC_ReadByte();
	IIC_Stop();
	return ReadData;
}
void PCF8563_WriteAdress(u8 Adress,u8 DataTX) //星
{
	IIC_Start();
	IIC_WriteByte(0xa2);
	IIC_WaitAck();
	IIC_WriteByte(Adress);
	IIC_WaitAck();
	IIC_WriteByte(DataTX);
	IIC_WaitAck();
	IIC_Stop();
}
//取回八个字节的数据:秒，分，时，天，星期，月份，年份。
void PCF8563_ReadTimes(void)
{
	IIC_Start();
	IIC_WriteByte(0xa2);
	IIC_WaitAck();
	IIC_WriteByte(0x02);
	IIC_WaitAck();
	IIC_Start();
	IIC_WriteByte(0xa3);
	IIC_WaitAck();
	PCF8563_Time[0] = IIC_ReadByte()&0x7f;
	I2C_Ack();
	PCF8563_Time[1] = IIC_ReadByte()&0x7f;
	I2C_Ack();
	PCF8563_Time[2] = IIC_ReadByte()&0x3f;
	I2C_Ack();
	PCF8563_Time[3] = IIC_ReadByte()&0x3f;
	I2C_Ack();
	PCF8563_Time[4] = IIC_ReadByte()&0x07;
	I2C_Ack();
	PCF8563_Time[5] = IIC_ReadByte()&0x1f;
	I2C_Ack();
	PCF8563_Time[6] = IIC_ReadByte();
	I2C_NoAck();
	IIC_Stop();
	PCF8563_Time[0] = ((PCF8563_Time[0]&0xf0)>>4)*10 + (PCF8563_Time[0]&0x0f); 
	PCF8563_Time[1] = ((PCF8563_Time[1]&0xf0)>>4)*10 + (PCF8563_Time[1]&0x0f);
	PCF8563_Time[2] = ((PCF8563_Time[2]&0xf0)>>4)*10 + (PCF8563_Time[2]&0x0f); 
	PCF8563_Time[3] = ((PCF8563_Time[3]&0xf0)>>4)*10 + (PCF8563_Time[3]&0x0f);
	PCF8563_Time[4] = ((PCF8563_Time[4]&0xf0)>>4)*10 + (PCF8563_Time[4]&0x0f); 
	PCF8563_Time[5] = ((PCF8563_Time[5]&0xf0)>>4)*10 + (PCF8563_Time[5]&0x0f); 
	PCF8563_Time[6] = ((PCF8563_Time[6]&0xf0)>>4)*10 + (PCF8563_Time[6]&0x0f); 
}
//在CLKOUT上定时1S输出一个下降沿脉冲，经过验证，可以设置STM32的GPIO上拉输入，设置成下降沿中断，单片机每过1S产生一次中断
void PCF8563_CLKOUT_1s(void)
{
	PCF8563_WriteAdress(0x01, 0); //禁止定时器输出，闹铃输出
	PCF8563_WriteAdress(0x0e, 0); //关闭定时器等等
	// PCF8563_WriteAdress(0x0e, 0); //写入1
	PCF8563_WriteAdress(0x0d, 0x83); //打开输出脉冲
}