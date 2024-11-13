import React, { useState, useEffect } from 'react';
import axiosInstance from "../axiosInstance";

function InsertOrder({ memberId }) {
    const [items, setItems] = useState([]);
    const [products, setProducts] = useState([]);
    const [totalPrice, setTotalPrice] = useState('');
    const [averagePrices, setAveragePrices] = useState({});
    const [error, setError] = useState(null);
    const [selectedProductName, setSelectedProductName] = useState('');
    const [lastMonthOrder, setLastMonthOrder] = useState([]);

    useEffect(() => {
        calculateTotalPrice();
    }, [items]);

    const id = localStorage.getItem('id');

    const handleRegister = async (e) => {
        e.preventDefault();
        const dataToSend = {
            items: items.map(item => ({
                productId: item.productId,
                quantity: item.quantity,
                price: item.price,
            })),
            id,
            totalPrice,
        };

        try {
            await axiosInstance.post('/api/v1/orders', dataToSend);
            alert('주문 등록 성공');
        } catch (error) {
            alert('주문 등록 실패');
        }
    };

    // /repeat API 호출 함수
    const fetchLastMonthOrder = async () => {
        try {
            const response = await axiosInstance.get('/api/v1/orders/repeat');
            setLastMonthOrder(response.data); // 데이터를 상태에 저장
            setError(null);
        } catch (error) {
            console.error('지난달 주문 내역 조회 실패:', error);
            setError('지난달 주문 내역 조회 실패');
        }
    };

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const response = await axiosInstance.get('/api/v1/products');
                setProducts(response.data.content);
            } catch (error) {
                console.error('상품 목록 조회 실패:', error);
            }
        };

        fetchProducts();
    }, []);

    const calculateTotalPrice = () => {
        const total = items.reduce((acc, item) => acc + item.price * item.quantity, 0);
        setTotalPrice(total);
    };

    const handleItemChange = (index, field, value) => {
        const newItems = [...items];
        const selectedProduct = products.find(product => product.name === value);

        if (field === 'productName' && selectedProduct) {
            const existingItemIndex = newItems.findIndex(item => item.productId === selectedProduct.id);
            if (existingItemIndex > -1) {
                newItems[existingItemIndex].quantity += 1;
            } else {
                newItems[index] = {
                    productId: selectedProduct.id,
                    quantity: 1,
                    price: selectedProduct.price,
                    productName: selectedProduct.name,
                };
            }
        } else {
            newItems[index][field] = value;
        }

        setItems(newItems);
        calculateTotalPrice();
    };

    const addItem = () => {
        setItems([...items, { productId: '', quantity: '', price: '' }]);
    };

    const removeItem = (index) => {
        const newItems = items.filter((_, i) => i !== index);
        setItems(newItems);
        calculateTotalPrice();
    };

    const fetchAveragePrices = async () => {
        try {
            const response = await axiosInstance.get('/api/v1/orders/average-prices');
            setAveragePrices(response.data);
            setError(null);
        } catch (error) {
            setError('평균 단가 조회 실패');
            console.error(error);
        }
    };

    // 지난달 주문 버튼 클릭 핸들러
    const handleFetchLastMonthOrder = async () => {
        try {
            const response = await axiosInstance.get('/api/v1/orders/repeat');
            // 지난달 주문 데이터 저장
            setLastMonthOrder(response.data);

            // 주문 입력 폼에 데이터 설정
            const formattedItems = response.data.flatMap(order =>
                order.orderItems.map(item => ({
                    productId: item.productId,
                    productName: item.product?.name,
                    quantity: item.quantity,
                    price: item.price
                }))
            );

            setItems(formattedItems); // 주문 폼에 데이터 설정
            calculateTotalPrice(); // 총 가격 다시 계산
        } catch (error) {
            console.error('지난달 주문 내역 조회 실패:', error);
            setError('지난달 주문 내역 조회 실패');
        }
    };

    return (
        <div className="insert-order-container">
            <h2 className="insert-order-header">주문 등록</h2>
            <form onSubmit={handleRegister}>
                <table className="insert-order-table">
                    <thead>
                    <tr>
                        <th>상품명</th>
                        <th>개수</th>
                        <th>가격</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    {items.map((item, index) => (
                        <tr key={index} className="insert-order-item">
                            <td>
                                <select
                                    value={item.productName || ''}
                                    onChange={(e) => handleItemChange(index, 'productName', e.target.value)}
                                >
                                    <option value="">상품 선택</option>
                                    {products.map((product) => (
                                        <option key={product.id} value={product.name}>
                                            {product.name}
                                        </option>
                                    ))}
                                </select>
                            </td>
                            <td>
                                <input
                                    type="number"
                                    value={item.quantity || ''}
                                    onChange={(e) => handleItemChange(index, 'quantity', Number(e.target.value))}
                                    placeholder="개수"
                                    required
                                />
                            </td>
                            <td>
                                <input
                                    type="number"
                                    value={item.price || ''}
                                    onChange={(e) => handleItemChange(index, 'price', Number(e.target.value))}
                                    placeholder="가격"
                                    required
                                />
                            </td>
                            <td>
                                <button type="button" onClick={() => removeItem(index)}>제거</button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
                <button type="button" onClick={addItem}>발주 물품 추가</button>

                <button type="button" onClick={handleFetchLastMonthOrder} >지난달 주문 가져오기</button>

                <h3 className="total-price">총 가격: {totalPrice}</h3>
                <button type="submit" className="insert-order-button">발주 등록</button>
            </form>

            <label htmlFor="product-select">상품명 선택:</label>
            <select id="product-select"
                    value={selectedProductName}
                    onChange={(e) => setSelectedProductName(e.target.value)}
            >
                <option value="">상품 선택</option>
                {products.map((product) => (
                    <option key={product.id} value={product.name}>
                        {product.name}
                    </option>
                ))}
            </select>

            <button onClick={fetchAveragePrices} className="average-prices-button">평균 단가 조회</button>


            {error && <div className="error-message">{error}</div>}
            <div className="average-prices">
                <table className="average-prices-table">
                    <thead>
                    <tr>
                        <th>월</th>
                        <th>상품명</th>
                        <th>평균 단가 (원)</th>
                    </tr>
                    </thead>
                    <tbody>
                    {Object.entries(averagePrices).map(([month, products]) => (
                        Object.entries(products).map(([productName, average]) => {
                            if (productName === selectedProductName) {
                                return (
                                    <tr key={`${month}-${productName}`}>
                                        <td>{month}</td>
                                        <td>{productName}</td>
                                        <td>{average.toFixed(2)}</td>
                                    </tr>
                                );
                            }
                            return null; // 선택한 상품이 아닐 경우 null 반환
                        })
                    ))}
                    </tbody>
                </table>
            </div>

            {/* 지난달 주문 목록 표시 */}
            {/*<h3>지난달 주문 내역</h3>*/}
            {/*<table className="last-month-order-table">*/}
            {/*    <thead>*/}
            {/*    <tr>*/}
            {/*        <th>상품명</th>*/}
            {/*        <th>개수</th>*/}
            {/*        <th>가격</th>*/}
            {/*    </tr>*/}
            {/*    </thead>*/}
            {/*    <tbody>*/}
            {/*    {lastMonthOrder.map((order, index) => (*/}
            {/*        // orderItems 배열을 순회*/}
            {/*        order.orderItems.map((item, itemIndex) => (*/}
            {/*            <tr key={`${index}-${itemIndex}`}>*/}
            {/*                <td>{item.product?.name || '상품 정보 없음'}</td>*/}
            {/*                <td>{item.quantity}</td>*/}
            {/*                <td>{item.price}</td>*/}
            {/*            </tr>*/}
            {/*        ))*/}
            {/*    ))}*/}
            {/*    </tbody>*/}
            {/*</table>*/}
        </div>
    );
}

export default InsertOrder;