import React, { useEffect, useState } from 'react';
import axiosInstance from "../axiosInstance";

function LossControl() {
    const [products, setProducts] = useState([]); // 제품 목록
    const [currentPage, setCurrentPage] = useState(0); // 현재 페이지
    const [totalPages, setTotalPages] = useState(0); // 전체 페이지 수
    const pageSize = 10; // 페이지당 제품 수
    const [searchTerm, setSearchTerm] = useState(''); // 검색어 상태
    const [isSearching, setIsSearching] = useState(false); // 검색 중인지 여부

    // 관리자용 제품 목록을 가져오는 함수
    const fetchLossControl = async (page) => {
        try {
            const response = await axiosInstance.get(`/api/adm/products/all`, {
                params: { page, size: pageSize },
            });

            console.log('Requested Page:', page); // 요청한 페이지 로그
            console.log('API Response:', response.data); // 응답 로그

            const updatedProducts = response.data.content.map(product => {
                const updatedLoss = product.loss === null || product.loss === 222 ? "등록된 로스율이 없습니다." : product.loss;
                return { ...product, loss: updatedLoss };
            });

            setProducts(updatedProducts);
            setTotalPages(response.data.totalPages);
        } catch (error) {
            console.error('Error fetching products:', error); // 에러
            alert('제품 목록을 불러오는 데 실패했습니다: ' + (error.response?.data?.message || '알 수 없는 오류'));
        }
    };

    // 검색 함수
    const searchProducts = async (page, keyword = '') => {
        try {
            const response = await axiosInstance.get(`/api/adm/products/search`, {
                params: { page, size: pageSize, keyword },
            });

            const updatedProducts = response.data.content.map(product => {
                const updatedLoss = product.loss === null || product.loss === 222 ? "등록된 로스율이 없습니다." : product.loss;
                return { ...product, loss: updatedLoss };
            });

            setProducts(updatedProducts);
            setTotalPages(response.data.totalPages);
        } catch (error) {
            alert('검색 결과를 불러오는 데 실패했습니다: ' + (error.response?.data?.message || '알 수 없는 오류'));
        }
    };

    useEffect(() => {
        if (isSearching) {
            searchProducts(currentPage, searchTerm); // 검색어를 기반으로 페이지 로드
        } else {
            fetchLossControl(currentPage); // 검색하지 않을 때는 기본 제품 목록 가져오기
        }
    }, [currentPage, isSearching, searchTerm]);

    // 검색어가 변경될 때 호출되는 함수
    const handleSearch = (e) => {
        const keyword = e.target.value;
        setSearchTerm(keyword); // 검색어 상태 업데이트
        setCurrentPage(0); // 검색 중에도 페이지를 처음부터 시작

        if (keyword.trim() === '') {
            setIsSearching(false); // 검색어가 비어 있으면 검색 모드 해제
            setCurrentPage(0); // 페이지를 0으로 설정하여 처음부터 목록을 불러옴
        }
    };

     // Enter 키를 누를 때 검색을 실행하는 함수
    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            handleSearchSubmit(); // Enter 키 입력 시 검색 실행
        }
    };

   // 검색 버튼 클릭 시 호출되는 함수
   const handleSearchSubmit = () => {
       if (searchTerm.trim() === '') {
           setIsSearching(false); // 검색어가 비어 있을 때는 검색 모드를 해제
           setCurrentPage(0); // 기본 목록으로 돌아갈 때 첫 페이지로 이동
       } else {
           setIsSearching(true); // 검색 모드로 전환
           searchProducts(0, searchTerm); // 검색 API 호출 시 초기 페이지로 설정
       }
   };

    return (
        <div>
            <h2>상품 목록</h2>
            <input
                type="text"
                value={searchTerm}
                onChange={handleSearch}
                onKeyDown={handleKeyDown}
                placeholder="조회할 상품명을 입력하세요"
                style={{ marginBottom: '16px', padding: '8px', width: '300px' }}
            />
            <button onClick={handleSearchSubmit} style={{ marginLeft: '8px' }}>
                검색
            </button>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                    <tr>
                        <th style={{ width: '25%', padding: '8px', textAlign: 'left' }}>상품 목록</th>
                        <th style={{ width: '25%', padding: '8px', textAlign: 'left' }}>로스율</th>
                    </tr>
                </thead>
                <tbody>
                    {products.length > 0 ? (
                        products.map((product, index) => (
                            <tr key={index}>
                                <td style={{ padding: '8px', border: '1px solid #ccc' }}>
                                    {product.name}
                                </td>
                                <td style={{ padding: '8px', border: '1px solid #ccc' }}>{product.loss}</td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="2" style={{ textAlign: 'center', padding: '8px' }}>등록된 상품이 없습니다</td>
                        </tr>
                    )}
                </tbody>
            </table>

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
}

export default LossControl;
