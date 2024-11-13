// src/components/Logout.js
import React from 'react';
import axiosInstance from "../axiosInstance";

function Logout({ onLogout }) {
    const handleLogout = async () => {
        const confirmLogout = window.confirm("로그아웃하시겠습니까?");
        if (!confirmLogout) return;

        try {
            // 서버에 로그아웃 요청
            await axiosInstance.post('/api/v1/members/logout', {}, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
                }
            });
        } catch (error) {
            console.error('서버 로그아웃 실패:', error);

        } finally {
            // 토큰 제거
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            // 사용자 상태 초기화
            onLogout();
            // 로그인 페이지로 리다이렉트
            window.location.href = '/login';
        }
    };

    return (
        <button
            className="delete-button"
            onClick={handleLogout}
        >
            로그아웃
        </button>
    );
}

export default Logout;
