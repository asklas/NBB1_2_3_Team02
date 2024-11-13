import React, { useState } from 'react';

function Register({ onRegister, handleBack }) {
    const [loginId, setLoginId] = useState('');
    const [pw, setPassword] = useState('');
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [loading, setLoading] = useState(false); // 로딩 상태 추가

    const handleRegister = async (e) => {
        e.preventDefault();
        setErrorMessage(''); // 이전 메시지 초기화
        setLoading(true); // 로딩 시작

        const dataToSend = {
            loginId,
            pw,
            name,
            email,
        };

        try {
            const response = await fetch('/api/v1/members/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(dataToSend),
            });

            if (response.status !== 200) {
                const errorData = await response.json();
                throw new Error(errorData.message || '회원가입 실패');
            }

            const responseData = await response.json();
            console.log('회원가입 성공:', responseData);
            const { id, name, accessToken, loginId } = responseData;
            localStorage.setItem('accessToken', accessToken);
            localStorage.setItem('id', id);
            localStorage.setItem('LoginId', loginId);

            alert(`환영합니다, ${dataToSend.name}!`);

            // 입력 필드 초기화
            setLoginId('');
            setPassword('');
            setEmail('');
            setName('');

            // 로그인 화면으로 이동
            onRegister();

        } catch (error) {
            setErrorMessage('회원가입 실패: ' + error.message);
        } finally {
            setLoading(false); // 로딩 종료
        }
    };

    return (
        <form onSubmit={handleRegister}>
            <div className="inputform">
                <div className='formInfo'>회원가입</div>
                <div>
                    <span className="input-info">이름:</span>
                    <input
                        className="auth-input"
                        type="text"
                        id="name"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        placeholder="이름을 입력하세요"
                        required
                    />
                </div>

                <div>
                    <span className="input-info">이메일:</span>
                    <input
                        className="auth-input"
                        type="text"
                        id="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="이메일을 입력하세요"
                        required
                    />
                </div>

                <div>
                    <span className="input-info">아이디:</span>
                    <input
                        className="auth-input"
                        type="text"
                        value={loginId}
                        onChange={(e) => setLoginId(e.target.value)}
                        placeholder="아이디를 입력하세요"
                        required
                    />
                </div>

                <div>
                    <span className="input-info">비밀번호:</span>
                    <input
                        className="auth-input"
                        type="password"
                        value={pw}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="비밀번호를 입력하세요"
                        required
                    />
                </div>
            </div>

            {errorMessage && <div style={{color: 'red'}}>{errorMessage}</div>}
            <div className="button-container">
                <button type="submit" disabled={loading}>
                    {loading ? '로딩 중...' : '회원가입'}
                </button>
                <button type="button" onClick={handleBack}>뒤로가기</button>
            </div>
        </form>
);
}

export default Register;
