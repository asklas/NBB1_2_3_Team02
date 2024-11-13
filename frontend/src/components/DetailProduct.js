import React, { useState, useEffect, useCallback } from 'react';
import axiosInstance from "../axiosInstance";
import { ResponsiveLine } from '@nivo/line';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import Modal from 'react-modal';

Modal.setAppElement('#root');

function DetailProduct({ product, onBack }) {
    const [productLoss, setProductLoss] = useState(product.loss);
    const [isUpdating, setIsUpdating] = useState(false); // 수정 모드 상태

    // 통계 관련 상태값
    const [statistics, setStatistics] = useState([]); // 통계 데이터
    const [startDate, setStartDate] = useState(new Date(new Date().setDate(new Date().getDate() - 7))); // 기본 시작일: 7일 전
    const [endDate, setEndDate] = useState(new Date()); // 기본 종료일: 오늘
    const [errorMessage, setErrorMessage] = useState(''); // 오류 메시지 관리

    useEffect(() => {
        // 로스율 등록 시 로스율 상태 업데이트
        if (product) {
            setProductLoss(product.loss);
        }
    }, [product]);

    const handleUpdateProduct = async (e) => {
        e.preventDefault();
        const dataToUpdate = {
            productId: product.id,
            loss: productLoss ? parseInt(productLoss) : null,
        };

        try {
            const response = await axiosInstance.post(`/api/v1/products/loss`, dataToUpdate, {
                headers: { 'Content-Type': 'application/json' },
            });

            console.log('응답데이터: ',response)

            alert('로스율이 등록되었습니다!');

            setProductLoss(productLoss); // 입력받은 로스율로 변경
            product.loss = productLoss ? productLoss : null;
            setIsUpdating(false); // 수정 모드 종료
        } catch (error) {
            alert('로스율 등록 실패: ' + (error.response.data || '알 수 없는 오류'));
        }
    };

    // 날짜 포매팅 함수
    const formatDateToLocalDateString = (date) => {
        return date.toLocaleDateString("ko-KR", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
            timeZone: "Asia/Seoul",
        })
        .replace(/\.\s?|\s/g, '-')
        .replace(/-$/, '');
    };

    // 통계 데이터 가져오는 함수 >> 매 랜더링 시 useEffect 재실행
    const fetchStatistics = useCallback(async (selectedStartDate, selectedEndDate) => {
        try {
            const adjustedStartDate = formatDateToLocalDateString(selectedStartDate)
            const adjustedEndDate = formatDateToLocalDateString(selectedEndDate)

            console.log('포맷된 시작 날짜:', adjustedStartDate);
            console.log('끝나는 날짜: ',adjustedEndDate)

            const response = await axiosInstance.get(`/api/v1/products/loss/${product.name}`, {
                params: {
                    startDate: adjustedStartDate,
                    endDate: adjustedEndDate,
                },
            });

            const data = response.data;
            console.log('응답 데이터2:', response.data);

            // 응답 데이터에서 차트에 필요한 형식으로 변환
            if (data.length > 0) {
                const formattedMyData = [];
                const formattedAllData = [];

                data.forEach(item => {
                    if (item.personalAverage && item.allUsersAverage) {
                        const myData = item.personalAverage.map((avg, index) => ({
                            x: formatDateToLocalDateString(new Date(item.dates[index])),
                            y: avg // 개인 평균 로스율
                        }));

                        const allData = item.allUsersAverage.map((avg, index) => ({
                            x: formatDateToLocalDateString(new Date(item.dates[index])),
                            y: avg // 전체 사용자 평균 로스율
                        }));
                        if (myData.length > 0 && allData.length > 0) {
                            formattedMyData.push(...myData);
                            formattedAllData.push(...allData);
                        }
                    }
                });

                // 데이터가 있는 경우에만 상태 업데이트
                if (formattedMyData.length > 0 || formattedAllData.length > 0) {
                    setStatistics([
                        { id: '개인 평균 로스율', data: formattedMyData },
                        { id: '전체 평균 로스율', data: formattedAllData },
                    ]);
                } else {
                    console.error("유효한 통계 데이터가 없습니다.");
                    setStatistics([]); // 통계 데이터를 초기화
                }

            } else {
                console.error("데이터 구조가 예상과 다릅니다:", data);
            }
        } catch (error) {
            console.error("에러 메시지:", error.message);
            setErrorMessage('통계 데이터를 불러오는 데 실패했습니다.');
        }
    }, [product.name]);

    // 통계 조회 버튼 클릭 시 처리
    const handleDateChange = () => {
        fetchStatistics(startDate, endDate);
    };

    useEffect(() => {
        // 날짜 변경 시 통계 데이터 가져오기
        fetchStatistics(startDate, endDate);
    }, [fetchStatistics, startDate, endDate, product.name]);

    const handleEditClick = () => {
        setProductLoss('');
        setIsUpdating(true);
    };

    if (!product) return null;

    const updatedLoss = product.loss === null || product.loss === 222 ? "등록된 로스율이 없습니다." : product.loss;

    return (
        <div>
            <h2>상품 상세 정보</h2>
            <h3>{product.name}</h3>
            <p>로스율: {updatedLoss}</p>

            <Modal
                isOpen={isUpdating}
                onRequestClose={() => setIsUpdating(false)}
                style={{
                    content: {
                        top: '50%',
                        left: '50%',
                        right: 'auto',
                        bottom: 'auto',
                        transform: 'translate(-50%, -50%)',
                        width: '600px', // 너비를 500px로 변경
                        height: '300px', // 높이 조정
                        padding: '30px', // 여백
                        border: '1px solid #ccc', // 테두리 추가
                        borderRadius: '8px', // 모서리 둥글게
                    },
                }}
            >
                <h2>오늘의 로스율</h2>
                <form onSubmit={handleUpdateProduct}>
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
                    <button type="button" onClick={() => setIsUpdating(false)}>취소</button>
                </form>
            </Modal>

            <button onClick={handleEditClick}>로스율 등록</button>

            <div style={{ marginTop: '20px' }}>
                <h3>로스율 통계</h3>
                <div style={{ display: 'flex', alignItems: 'center', marginBottom: '20px' }}>
                    <div style={{ marginRight: '10px' }}>
                        <label>시작 날짜:</label>
                        <DatePicker
                            selected={startDate}
                            onChange={date => setStartDate(date)}
                            dateFormat="yyyy-MM-dd"
                        />
                    </div>
                    <div style={{ marginRight: '10px' }}>
                        <label>종료 날짜:</label>
                        <DatePicker
                            selected={endDate}
                            onChange={date => setEndDate(date)}
                            dateFormat="yyyy-MM-dd"
                        />
                    </div>
                    <button onClick={handleDateChange}>통계 조회</button>
                </div>

                {errorMessage && (
                    <p style={{ color: 'red', fontWeight: 'bold' }}>
                        {errorMessage}
                    </p>
                )}

                <div style={{ height: '400px', width: '600px' }}> {/* 차트 크기 조절 */}
                    {statistics.length > 0 ? (
                        <ResponsiveLine
                            data={statistics} // 통계 데이터를 전달
                            margin={{ top: 20, right: 120, bottom: 60, left: 70 }}
                            xScale={{ type: 'point' }}
                            yScale={{ type: 'linear', min: 'auto', max: 'auto', stacked: false, reverse: false }}
                            axisBottom={{
                                orient: 'bottom',
                                legend: '날짜',
                                legendOffset: 36,
                                legendPosition: 'middle',
                                format: (value) => new Date(value).toLocaleDateString(), // 날짜 포맷
                            }}
                            axisLeft={{
                                orient: 'left',
                                legend: '로스율',
                                legendOffset: -40,
                                legendPosition: 'middle',
                            }}
                            enablePoints={true}
                            useMesh={true}
                            enableGridX={false}
                            enableGridY={true}
                            pointSize={10}
                            pointColor={{ theme: 'background' }}
                            pointBorderWidth={2}
                            pointBorderColor={{ from: 'serieColor' }}
                            enableArea={true}
                            areaOpacity={0.1}
                            areaBlendMode="multiply"
                            colors={{ scheme: 'nivo' }}
                            legends={[
                                {
                                    anchor: 'bottom-right',  // 범례 위치
                                    direction: 'column',     // 범례 항목의 방향
                                    justify: false,
                                    translateX: 90,         // 차트에서 얼마나 떨어져 있을지
                                    translateY: 0,
                                    itemsSpacing: 0,         // 항목 간 간격
                                    itemDirection: 'left-to-right',
                                    itemWidth: 80,           // 각 항목의 너비
                                    itemHeight: 20,          // 각 항목의 높이
                                    itemOpacity: 0.75,       // 항목의 불투명도
                                    symbolSize: 10,          // 색상 원 크기
                                    symbolShape: 'circle',   // 원형 마커
                                    symbolBorderColor: 'rgba(0, 0, 0, .5)',
                                    effects: [
                                        {
                                            on: 'hover',
                                            style: {
                                                itemBackground: 'rgba(0, 0, 0, .03)',
                                                itemOpacity: 1
                                            }
                                        }
                                    ]
                                }
                            ]}
                        />
                    ) : (
                        <p>누적된 데이터가 부족합니다.</p> // 통계 데이터가 없을 경우 메시지
                    )}

                    {console.log('Statistics data:', statistics)}
                    <button onClick={onBack}>목록으로 돌아가기</button>
                </div>
            </div>
        </div>
    );
}

export default DetailProduct;