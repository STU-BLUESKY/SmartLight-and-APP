#include "sys.h"
#include "usart.h"	 
#include "24cxx.h"
////////////////////////////////////////////////////////////////////////////////// 	 
//如果使用ucos,则包括下面的头文件即可.
#if SYSTEM_SUPPORT_OS
#include "includes.h"					//ucos 使用	  
#endif
//////////////////////////////////////////////////////////////////////////////////	 
//本程序只供学习使用，未经作者许可，不得用于其它任何用途
//ALIENTEK STM32开发板
//串口1初始化		   
//正点原子@ALIENTEK
//技术论坛:www.openedv.com
//修改日期:2012/8/18
//版本：V1.5
//版权所有，盗版必究。
//Copyright(C) 广州市星翼电子科技有限公司 2009-2019
//All rights reserved
//********************************************************************************
//V1.3修改说明 
//支持适应不同频率下的串口波特率设置.
//加入了对printf的支持
//增加了串口接收命令功能.
//修正了printf第一个字符丢失的bug
//V1.4修改说明
//1,修改串口初始化IO的bug
//2,修改了USART_RX_STA,使得串口最大接收字节数为2的14次方
//3,增加了USART_REC_LEN,用于定义串口最大允许接收的字节数(不大于2的14次方)
//4,修改了EN_USART1_RX的使能方式
//V1.5修改说明
//1,增加了对UCOSII的支持
////////////////////////////////////////////////////////////////////////////////// 	  
 

//////////////////////////////////////////////////////////////////
//加入以下代码,支持printf函数,而不需要选择use MicroLIB	  
#if 1
#pragma import(__use_no_semihosting)             
//标准库需要的支持函数  

u16 RECEIVE_CCR1_VAL;
u16 RECEIVE_CCR2_VAL;
u16 RECEIVE_CCR3_VAL;
u16 RECEIVE_CCR4_VAL;
u16 receive_state=0;
extern u16 power;
extern u8 liangduzidong_flag;
extern u8 xiaoyedeng_flag;
extern u8 close_time_hour;
extern u8 close_time_min;
extern u8 open_time_hour;
extern u8 open_time_min;
extern u8 open_time_change_flag;
extern u8 close_time_change_flag;
extern u8 liangduzidong_flag_change;
extern u8 xiaoyedeng_flag_change;
extern u8 yanseziduojianbian_flag;
extern u8 yanseziduojianbian_flag_change;

struct __FILE 
{ 
	int handle; 

}; 

FILE __stdout;       
//定义_sys_exit()以避免使用半主机模式    
_sys_exit(int x) 
{ 
	x = x; 
} 
//重定义fputc函数 
int fputc(int ch, FILE *f)
{      
	while((USART1->SR&0X40)==0);//循环发送,直到发送完毕   
    USART1->DR = (u8) ch;      
	return ch;
}
#endif 

 
 
#if EN_USART1_RX   //如果使能了接收
//串口1中断服务程序
//注意,读取USARTx->SR能避免莫名其妙的错误   	
u8 USART_RX_BUF[USART_REC_LEN];     //接收缓冲,最大USART_REC_LEN个字节.
//接收状态
//bit15，	接收完成标志
//bit14，	接收到0x0d
//bit13~0，	接收到的有效字节数目
u16 USART_RX_STA=0;       //接收状态标记	  
  
void uart_init(u32 bound){
  //GPIO端口设置
  GPIO_InitTypeDef GPIO_InitStructure;
	USART_InitTypeDef USART_InitStructure;
	NVIC_InitTypeDef NVIC_InitStructure;
	 
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_USART1|RCC_APB2Periph_GPIOA, ENABLE);	//使能USART1，GPIOA时钟
  
	//USART1_TX   GPIOA.9
  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_9; //PA.9
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF_PP;	//复用推挽输出
  GPIO_Init(GPIOA, &GPIO_InitStructure);//初始化GPIOA.9
   
  //USART1_RX	  GPIOA.10初始化
  GPIO_InitStructure.GPIO_Pin = GPIO_Pin_10;//PA10
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN_FLOATING;//浮空输入
  GPIO_Init(GPIOA, &GPIO_InitStructure);//初始化GPIOA.10  

  //Usart1 NVIC 配置
  NVIC_InitStructure.NVIC_IRQChannel = USART1_IRQn;
	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority=3 ;//抢占优先级3
	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 3;		//子优先级3
	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;			//IRQ通道使能
	NVIC_Init(&NVIC_InitStructure);	//根据指定的参数初始化VIC寄存器
  
   //USART 初始化设置

	USART_InitStructure.USART_BaudRate = bound;//串口波特率
	USART_InitStructure.USART_WordLength = USART_WordLength_8b;//字长为8位数据格式
	USART_InitStructure.USART_StopBits = USART_StopBits_1;//一个停止位
	USART_InitStructure.USART_Parity = USART_Parity_No;//无奇偶校验位
	USART_InitStructure.USART_HardwareFlowControl = USART_HardwareFlowControl_None;//无硬件数据流控制
	USART_InitStructure.USART_Mode = USART_Mode_Rx | USART_Mode_Tx;	//收发模式

  USART_Init(USART1, &USART_InitStructure); //初始化串口1
  USART_ITConfig(USART1, USART_IT_RXNE, ENABLE);//开启串口接受中断
  USART_Cmd(USART1, ENABLE);                    //使能串口1 

}

void USART1_IRQHandler(void)                	//串口1中断服务程序
{
	u8 Res;
#if SYSTEM_SUPPORT_OS 		//如果SYSTEM_SUPPORT_OS为真，则需要支持OS.
	OSIntEnter();    
#endif
	if(USART_GetITStatus(USART1, USART_IT_RXNE) != RESET)  //接收中断(接收到的数据必须是0x0d 0x0a结尾)
	{
		Res =USART_ReceiveData(USART1);	//读取接收到的数据			
		switch(receive_state)
		{
			case 0:
				if(Res==0x0d)//数据头
				{
					receive_state++;
				}
				else if(Res==0x03)
				{
					receive_state=5;//亮度自动
				}
				else if(Res==0x02)
				{
					receive_state=6;//小夜灯
				}
				else if(Res==0xaa)//170
				{
					receive_state=7;//定时开灯
				}
				else if(Res==0xcc)//204
				{
					receive_state=9;//定时关灯
				}
				else if(Res==0x04)
				{
					receive_state=11;//颜色渐变
				}
				break;
			case 1:
				RECEIVE_CCR1_VAL=Res;
				receive_state++;
				break;
			case 2:
				RECEIVE_CCR2_VAL=Res;
				receive_state++;
				break;
			case 3:
				RECEIVE_CCR3_VAL=Res;
				receive_state++;
				break;
			case 4:
				power=Res;
				receive_state=12;
				break;
			case 5:           //亮度自动
				liangduzidong_flag_change=1;
				liangduzidong_flag=Res;				
				receive_state=12;
				break;
			case 6:          //小夜灯
				xiaoyedeng_flag_change=1;
				xiaoyedeng_flag=Res;				
				receive_state=12;
				break;
			case 7:        //定时开灯		
				open_time_hour=Res;				
				receive_state=8;
				break;
			case 8:
				open_time_change_flag=1;
				open_time_min=Res;				
				receive_state=12;
				break;
			case 9:        //定时关灯
				close_time_hour=Res;
				receive_state=10;
				break;
			case 10:
				close_time_change_flag=1;
				close_time_min=Res;			
				receive_state=12;
				break;
			case 11:
				yanseziduojianbian_flag_change=1;
				yanseziduojianbian_flag=Res;
				receive_state=12;
				break;
			case 12:
				if(Res==0x0a)//数据尾
				{
					receive_state=0;
				}
				break;
		}
	
	} 
#if SYSTEM_SUPPORT_OS 	//如果SYSTEM_SUPPORT_OS为真，则需要支持OS.
	OSIntExit();  											 
#endif
} 
#endif	

