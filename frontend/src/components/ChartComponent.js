import React, { useRef } from "react";
import { Line } from 'react-chartjs-2';

const ChartComponent = ({ data }) => {
    const chartRef = useRef(null);

    // 1~12월의 라벨 생성
    const labels = [
        '1월', '2월', '3월', '4월', '5월', '6월',
        '7월', '8월', '9월', '10월', '11월', '12월'
    ];

    // 월별 총 금액을 가져오기 위해 0으로 초기화
    const monthlyData = new Array(12).fill(0);

    // data를 순회하며 각 월의 총 금액을 설정
    data.forEach(item => {
        const monthIndex = new Date(item.orderMonth).getMonth(); // 0~11 사이의 인덱스
        monthlyData[monthIndex] = item.totalPrice; // 해당 월의 총 금액 설정
    });

    const chartData = {
        labels, // 1~12월 라벨 사용
        datasets: [
            {
                label: 'Total Price',
                data: monthlyData, // 월별 총 금액 데이터
                borderColor: '#8884d8',
                backgroundColor: 'rgba(136, 132, 216, 0.2)',
                fill: true,
            },
        ],
    };

    const chartOptions = {
        responsive: true,
        plugins: {
            legend: {
                position: 'top',
            },
        },
        scales: {
            x: {
                title: {
                    display: true,
                    text: '월별 주문',
                },
            },
            y: {
                title: {
                    display: true,
                    text: '총 금액',
                },
                beginAtZero: true,
            },
        },
    };

    return (
        <div className="chart-container">
            <h2 className="chart-title">월별 총 금액 차트</h2>
            <Line ref={chartRef} data={chartData} options={chartOptions} />
        </div>
    );
};

export default ChartComponent;
