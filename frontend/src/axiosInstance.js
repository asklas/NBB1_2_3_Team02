// src/axiosInstance.js
import axios from 'axios';

// 기본 Axios 인스턴스 생성
const axiosInstance = axios.create({
    baseURL: '/', // 필요에 따라 기본 URL 설정
});

// 요청 인터셉터: 요청 시 accessToken을 헤더에 첨부
axiosInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// 응답 인터셉터: 401 에러 발생 시 accessToken 갱신 시도
axiosInstance.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        // 이미 갱신 시도를 했으면 무한 루프 방지
        if (error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
            const refreshToken = localStorage.getItem('refreshToken');

            if (refreshToken) {
                try {
                    const response = await axios.post('/api/v1/members/refreshAccessToken', { refreshToken });
                    const { accessToken: newAccessToken } = response.data;

                    // 새로운 accessToken 저장
                    localStorage.setItem('accessToken', newAccessToken);

                    // 새로운 토큰으로 헤더 업데이트
                    axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${newAccessToken}`;
                    originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;

                    // 원래 요청 재시도
                    return axiosInstance(originalRequest);
                } catch (refreshError) {
                    console.error('accessToken 갱신 실패:', refreshError);
                    // 토큰 갱신 실패 시 로그아웃 처리 등 추가 로직 필요
                    localStorage.removeItem('accessToken');
                    localStorage.removeItem('refreshToken');
                    window.location.href = '/login'; // 로그인 페이지로 리다이렉트
                    return Promise.reject(refreshError);
                }
            } else {
                // refreshToken이 없을 경우 로그인 페이지로 리다이렉트
                window.location.href = '/login';
            }
        }

        return Promise.reject(error);
    }
);

export default axiosInstance;