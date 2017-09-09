#include "IIC.h"
#include "delay.h"
/**/
void IIC_Init(void)
{
	GPIO_InitTypeDef GPIO_InitStructure;
	/* Configure I2C1 pins: SCL and SDA */
	RCC_APB2PeriphClockCmd (RCC_APB2Periph_GPIOC, ENABLE);
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_7 | GPIO_Pin_8;
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_OD; //开漏输出，可以在不用改变成输入的情况下读取IO的电平
	GPIO_Init(GPIOC, &GPIO_InitStructure);
	SCL_H(); //拉高
	SDA_H();
}
void IIC_Start(void)
{
	/* SDA_H();
	SCL_H();
	delay_nus(20);
	SDA_L();
	delay_nus(20);
	*/
	SDA_H();
	SCL_H();
	delay_us(2);
	SDA_L();
	delay_us(2);
	SDA_L();
	delay_us(2);
}
void IIC_Stop(void)
{
	SCL_L(); //1
	delay_us(2);// 2
	SDA_L(); // 3. 1,2,3这三行不可缺少 delay_nus(20);
	SCL_H();
	delay_us(2);
	SDA_H();
	delay_us(2);
}
void IIC_WaitAck(void)
{
	u16 k;
	SCL_L();
	SDA_H();
	delay_us(2);
	SCL_H();
	k = 0;
	while((Read_SDA()!= 0) && (k < 60000))k++; delay_us(20);
	SCL_L();
	delay_us(2);
}
void IIC_WriteByte(u8 byte)
{
	u8 i = 0;
	for(i = 0; i < 8; i++)
	{
		SCL_L();
		delay_us(2);
		if(byte & 0x80)
		{
			SDA_H();
		}
		else
		{
			SDA_L();
		}
		delay_us(2);
		SCL_H();
		delay_us(2);
		byte<<=1;
	}
	SCL_L();
	delay_us(2);
}
u8 IIC_ReadByte(void)
{
	u8 i,ReadByte;
	SDA_H();
	for(i = 0; i < 8; i++)
	{
		ReadByte <<= 1;
		SCL_L();
		delay_us(2);
		SCL_H();
		delay_us(2);
		if(Read_SDA())
		{
		ReadByte |= 0x01;
		}
		else
		{
			ReadByte &= ~(0x01); 
		}
	}
	return ReadByte;
}
void I2C_Ack(void)
{
	SCL_L();
	delay_us(2);
	SDA_L();
	delay_us(2);
	SCL_H();
	delay_us(2);
	SCL_L();
	delay_us(2);
}
void I2C_NoAck(void)
{
	SCL_L();
	delay_us(2);
	SDA_H();
	delay_us(2);
	SCL_H();
	delay_us(2);
	SCL_L();
	delay_us(2);
}