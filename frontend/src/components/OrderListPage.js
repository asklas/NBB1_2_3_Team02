import React, { useEffect, useState } from 'react';
import OrderTable from './OrderTable';
import axiosInstance from "../axiosInstance";

const OrderListPage = () => {
    const [orders, setOrders] = useState([]);
    const [products, setProducts] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0); // 총 페이지 수

    const ordersPerPage = 10; // 페이지당 주문 수

    useEffect(() => {
        const fetchOrderList = async (page) => {
            try {
                const response = await axiosInstance.get(`/api/v1/orders/list?page=${page}&size=${ordersPerPage}`);
                setOrders(response.data.content);
                setTotalPages(response.data.totalPages); // 총 페이지 수
            } catch (error) {
                console.error('주문 목록 조회 실패:', error);
            }
        };

        const fetchProducts = async () => {
            try {
                const response = await axiosInstance.get('/api/v1/products');
                setProducts(response.data.content);
            } catch (error) {
                console.error('상품 목록 조회 실패:', error);
            }
        };

        fetchOrderList(currentPage + 1); // currentPage + 1로 API 호출
        fetchProducts();
    }, [currentPage]);

    return (
        <div className="order-check-container">
            <h2>주문 목록</h2>
            <OrderTable orders={orders} products={products}/>

            {/* 페이지네이션 */}
            <div style={{
                marginTop: '16px',
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                gap: '8px' // 버튼 사이의 간격을 추가 (선택 사항)
            }}>
                <button
                    onClick={() => setCurrentPage(currentPage - 1)}
                    disabled={currentPage <= 0}
                >
                    이전
                </button>
                <span> 페이지 {currentPage + 1} / {totalPages} </span>

                <button
                    onClick={() => setCurrentPage(currentPage + 1)}
                    disabled={currentPage >= totalPages - 1}
                >
                    다음
                </button>
            </div>
        </div>
    );
};

export default OrderListPage;
