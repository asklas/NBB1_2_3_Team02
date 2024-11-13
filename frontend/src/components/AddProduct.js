import React, { useState } from 'react';
import axiosInstance from "../axiosInstance";

function AddProduct() {
    const [productName, setProductName] = useState('');
    const [productLoss, setProductLoss] = useState('');

    const handleAddProduct = async (e) => {
        e.preventDefault();

        const dataToSend = {
            name: productName.trim(),
            loss: productLoss ? parseInt(productLoss) : null,
        };

        try {
            await axiosInstance.post('/api/v1/products/register', dataToSend, {});

            alert('식재료가 등록되었습니다!');
            setProductName(''); // 등록창 상태 초기화
            setProductLoss('');
        } catch (error) {
            alert('식재료 등록 실패: ' + (error.response.data || '알 수 없는 오류'));
        }
    };

    return (
        <div>
            <h2>식재료 등록</h2>
            <form onSubmit={handleAddProduct}>
                <div>
                    <label>식재료 이름:</label>
                    <input
                        type="text"
                        value={productName}
                        onChange={(e) => setProductName(e.target.value)}
                        placeholder="식재료 이름을 입력하세요"
                        required
                    />
                </div>
                <div>
                    <label>로스율:</label>
                    <input
                        type="number"
                        value={productLoss}
                        onChange={(e) => setProductLoss(e.target.value)}
                        placeholder="로스율을 입력하세요"
                    />
                </div>
                <button type="submit" className="button">등록하기</button>
            </form>
        </div>
    );
}

export default AddProduct;
