import React, { useState } from 'react';

const OrderTable = ({ orders, products }) => {
    const [openOrderId, setOpenOrderId] = useState(null); // 상태 추가

    const getProductNameById = (productId) => {
        const product = products.find(product => product.id === productId);
        return product ? product.name : 'N/A';
    };

    const toggleOrderDetails = (orderId) => {
        setOpenOrderId(openOrderId === orderId ? null : orderId); // 클릭한 주문 ID 토글
    };

    return (
        <table className="tableStyle">
            <thead className="tableheader">
            <tr>
                <th>주문 번호</th>
                <th>총 가격</th>
                <th>주문일</th>
            </tr>
            </thead>
            <tbody>
            {orders.length === 0 ? (
                <tr>
                    <td colSpan="5">주문 항목이 없습니다.</td>
                </tr>
            ) : (
                orders.map(order => (
                    <React.Fragment key={order.id}>
                        <tr onClick={() => toggleOrderDetails(order.id)} style={{cursor: 'pointer'}}>
                            <td className="nestedordertable">{order.id}</td>
                            <td className="nestedordertable">{order.totalPrice}</td>
                            <td className="nestedordertable">{new Date(order.createdAt).toLocaleString()}</td>
                        </tr>
                        {openOrderId === order.id && order.orderItems && order.orderItems.length > 0 && (
                            <tr>
                                <td colSpan="5">
                                    <table className="nestedTableStyle">

                                        <thead className="nestedtable-header">
                                        <tr>
                                            <th>상품 이름</th>
                                            <th>수량</th>
                                            <th>가격</th>
                                        </tr>
                                        </thead>
                                        <tbody className="nestedtable-body">
                                        {order.orderItems.map(item => (
                                            <tr key={item.productId}>
                                                <td>{getProductNameById(item.productId)}</td>
                                                <td>{item.quantity}</td>
                                                <td>{item.price}</td>
                                            </tr>
                                        ))}
                                        </tbody>

                                    </table>
                                </td>
                            </tr>
                        )}
                    </React.Fragment>
                ))
            )}
            </tbody>
        </table>
    );
};



export default OrderTable;
