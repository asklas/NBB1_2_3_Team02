import React, { useState } from 'react';
import axios from "axios";

const FindIdPw = ({ handleBack }) => {
    const [email, setEmail] = useState('');
    const [loginId, setLoginId] = useState('');
    const [message, setMessage] = useState('');
    const [templatePassword, setTemplatePassword] = useState('');
    const [isFindingId, setIsFindingId] = useState(true); // 상태 추가

    const handleFindId = async () => {
        try {
            const response = await axios.get(`/api/v1/members/findId?email=${email}`);
            setLoginId(response.data);
        } catch (error) {
            setMessage('아이디 찾기에 실패하였습니다.');
        }
    };

    const handleFindPw = async () => {
        try {
            const response = await axios.post('/api/v1/members/findPW', { loginId, email });
            setTemplatePassword(response.data);
            setMessage('임시 비밀번호가 이메일로 전송되었습니다.');
        } catch (error) {
            setMessage('비밀번호 찾기에 실패하였습니다.');
        }
    };

    const toggleFindingMethod = () => {
        setIsFindingId(!isFindingId);
        setMessage(''); // 메시지 초기화
        setLoginId(''); // 로그인 ID 초기화
        setEmail(''); // 이메일 초기화
    };

    return (
        <div className="FindId-container">


            {isFindingId ? (
                <div>
                    <h2 className="FindId-h2">ID 찾기</h2>
                    <div>
                        <label className="FindId-label">이메일:</label>
                        <input
                            className="FindId-input"
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>
                    <div>
                        <button className="FindId-button" onClick={handleFindId}>아이디 찾기</button>
                    </div>
                    {loginId && <p className="FindId-message">아이디 <br/> {loginId}</p>}
                </div>
            ) : (
                <div>
                    <h2 className="FindId-h2">PW 찾기</h2>
                    <div>
                        <label className="FindId-label">아이디:</label>
                        <input
                            className="FindId-input"
                            type="text"
                            value={loginId}
                            onChange={(e) => setLoginId(e.target.value)}
                            required
                        />
                    </div>
                    <div>
                        <label className="FindId-label">이메일:</label>
                        <input
                            className="FindId-input"
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>
                    <div>
                        <button className="FindId-button" onClick={handleFindPw}>비밀번호 찾기</button>
                    </div>
                </div>
            )}
            {message && <p className="FindId-message">{message}</p>}

            {/* 버튼 위치 고정 */}
            <div style={{ position: 'absolute', bottom: '20px', left: '67%', transform: 'translateX(-50%)', display: 'flex', width: '100%' }}>
                <button className="FindId-button" onClick={toggleFindingMethod} style={{ marginRight: '10px' }}>
                    {isFindingId ? '비밀번호 찾기' : '아이디 찾기'}
                </button>
                <button className="FindId-button" onClick={handleBack}>
                    뒤로가기
                </button>
            </div>
        </div>
    );
};

export default FindIdPw;