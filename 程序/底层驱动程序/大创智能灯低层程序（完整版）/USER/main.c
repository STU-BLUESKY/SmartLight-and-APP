#include "public.h"
#include "timer.h"
#include "usart.h"
#include "adc.h"
#include "PCF8563.h"
#include "delay.h"
#include "24cxx.h"

/*
	电源接5V，(从左往右)   VCC->5V  PA6   PB00  PA7
	PCF8563： SDA->PC8, SCL->PC7, VCC->3.3V
	24C08:    (从左往右)  VCC->3.3V   GND->地   红线->PC12    蓝色->PC11
	HC05:      VCC->3.3V   GND->地    TX->PA10   RX->PA9
	光敏电阻:  (从左往右)  AO->PA00      GND->地     VCC->3.3
*/


u16 led0pwmval=0;    
u8 dir_1=1;	 
u8 dir_2=1;
u8 dir_3=1;
u8 dir_4=1;
u16 CCR1_VAL=0;//R
u16 CCR2_VAL=0;//B
u16 CCR3_VAL=124;//G
u16 CCR4_VAL=500;
u16 power=1;
u8 liangduzidong_flag=0;//亮度自动功能标志位
u8 xiaoyedeng_flag=0;//小夜灯标志位
u8 PCF8563_Time[7] = {15, 38, 21, 28, 5, 4, 17};
u8 close_time_hour=0;
u8 close_time_min=0;
u8 open_time_hour=0;
u8 open_time_min=0;

u8 open_time_change_flag=0;
u8 close_time_change_flag=0;
u8 liangduzidong_flag_change=0;
u8 xiaoyedeng_flag_change=0;
u8 yanseziduojianbian_flag_change;
u8 yanseziduojianbian_flag=0;//颜色自动渐变功能标志位

//下面这两组变量是用来实现颜色渐变的，其实可以与上面的对应的变量合并
//HSL，HSL是色调，饱和度和亮度的简称
double CCR_H=0;
double CCR_S=0;
double CCR_L=0;
//RBG
double CCR_R=255;//初始化为红色
double CCR_B=0;
double CCR_G=0;


/*
*用于测试
*/
void LED_Init(void)
{
	GPIO_InitTypeDef  GPIO_InitStructure;
		
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB, ENABLE);	 //使能PA,PD端口时钟
		
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_12|GPIO_Pin_13|GPIO_Pin_14;				 //LED0-->PA.8 端口配置
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP; 		 //推挽输出
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;		 //IO口速度为50MHz
	GPIO_Init(GPIOB, &GPIO_InitStructure);					 //根据设定参数初始化GPIOA.8
	GPIO_ResetBits(GPIOB,GPIO_Pin_8);						 //PA.8 输出高 						 //PD.2 输出高 
}

int main(void)
{
	SystemInit();//初始化RCC 设置系统主频为72MHZ
	delay_init();
//
	PCF8563_Init();	//仅需调用一次
	IIC_Init();
	Adc_Init();
	AT24CXX_Init();
	open_time_hour=AT24CXX_ReadOneByte(0);
	open_time_min=AT24CXX_ReadOneByte(1);
	close_time_hour=AT24CXX_ReadOneByte(2);
	close_time_min=AT24CXX_ReadOneByte(3);
	liangduzidong_flag=AT24CXX_ReadOneByte(4);
	xiaoyedeng_flag=AT24CXX_ReadOneByte(5);
	yanseziduojianbian_flag=AT24CXX_ReadOneByte(6);
	LED_Init();
	GPIO_ResetBits(GPIOA,GPIO_Pin_8);
	uart_init(9600);
	TIM3_GPIO_Config();
	TIM3_Mode_Config();	
	Timer2_Cfg();		
	while(1);
}

































