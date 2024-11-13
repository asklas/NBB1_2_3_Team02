import React, { useEffect, useState } from "react";
import axiosInstance from "../axiosInstance";
import ChartComponent from './ChartComponent';
import 'chart.js/auto';


const OrderSummary = () => {
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedMonth, setSelectedMonth] = useState(new Date().toISOString().slice(0, 7)); // 현재 월로 기본값 설정

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axiosInstance.get('/api/v1/orders/monthly-summary', {
                    params: {
                        month: selectedMonth // 선택한 월을 전달
                    }
                });
                setData(response.data);
            } catch (error) {
                console.error('Error fetching monthly summary:', error);
                setError('데이터를 가져오는 데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    },[]);

    if (loading) return <p>Loading...</p>;
    if (error) return <p>{error}</p>;
    if (!data || data.length === 0) return <p>데이터가 없습니다.</p>;

    return (
        <div>
            <ChartComponent data={data} />
        </div>
    );
};

export default OrderSummary;
