import React, { useState, useEffect } from 'react';
import QnADetail from './QnADetail';
import axiosInstance from "../axiosInstance";
import './QnAPage.css'; // 새로운 CSS 파일 import

const QnAPage = () => {
    const [questions, setQuestions] = useState([]);
    const [newQuestion, setNewQuestion] = useState({ title: '', description: '' });
    const [showAddQuestion, setShowAddQuestion] = useState(false); // 질문 추가 폼 표시 여부
    const [errorMessage, setErrorMessage] = useState('');
    const [showDetail, setShowDetail] = useState(null);

    // 질문 목록 가져오기
    useEffect(() => {
        const fetchQuestions = async () => {
            try {
                const response = await axiosInstance.get('/api/v1/qna/all');
                setQuestions(response.data.questions);
            } catch (error) {
                setErrorMessage('질문을 가져오는 데 실패했습니다.');
            }
        };

        fetchQuestions();
    }, []);

    // 질문 추가
    const handleAddQuestion = async (e) => {
        e.preventDefault();
        try {
            const response = await axiosInstance.post('/api/v1/qna/post', newQuestion);
            setQuestions([...questions, response.data]);
            setNewQuestion({ title: '', description: '' });
            setShowAddQuestion(false); // 질문 추가 후 폼 숨기기
        } catch (error) {
            setErrorMessage('질문 추가에 실패했습니다.');
        }
    };

    // 취소 버튼 핸들러
    const handleCancel = () => {
        setShowAddQuestion(false);
        setNewQuestion({ title: '', description: '' }); // 입력 초기화
    };

    const updateQuestions = (updatedQuestion, deletedQuestionId) => {
        if (updatedQuestion) {
            setQuestions(questions.map(q => (q.id === updatedQuestion.id ? updatedQuestion : q)));
        }
        if (deletedQuestionId) {
            setQuestions(questions.filter(q => q.id !== deletedQuestionId));
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'ANSWERED':
                return 'green'; // 답변됨
            case 'IN_PROGRESS':
                return 'orange'; // 문제 해결중
            case 'UNANSWERED':
                return 'red'; // 답변되지 않음
            default:
                return 'black'; // 기본 색상
        }
    };

    return (
        <div className="qna-container">
            <h1 className="qna-header">QnA 게시판</h1>
            {errorMessage && <div className="qna-error">{errorMessage}</div>}

            {!showAddQuestion ? (
                <button className="qna-button" onClick={() => setShowAddQuestion(true)}>
                    질문 게시
                </button>
            ) : (
                <form onSubmit={handleAddQuestion}>
                    <div>
                        <input
                            type="text"
                            placeholder="제목"
                            value={newQuestion.title}
                            onChange={(e) => setNewQuestion({ ...newQuestion, title: e.target.value })}
                            required
                        />
                    </div>
                    <div>
                        <textarea
                            placeholder="설명"
                            value={newQuestion.description}
                            onChange={(e) => setNewQuestion({ ...newQuestion, description: e.target.value })}
                            required
                        />
                    </div>
                    <button type="submit" className="qna-button">질문 추가하기</button>
                    <button type="button" className="qna-button" onClick={handleCancel}>취소</button>
                </form>
            )}

            {showDetail && (
                <QnADetail question={showDetail} onClose={() => setShowDetail(null)} onUpdateQuestions={updateQuestions} />
            )}

            <h2 className="qna-subheader">질문 목록</h2>
            <ul className="qna-list">
                {questions.map(question => (
                    <li key={question.id} className="qna-item">
                        <h3 onClick={() => setShowDetail(question)} style={{cursor: 'pointer'}}>
                            {question.title}
                            <span style={{marginLeft: '10px', color: getStatusColor(question.status)}}>
                                {question.status}
                            </span>
                        </h3>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default QnAPage;
