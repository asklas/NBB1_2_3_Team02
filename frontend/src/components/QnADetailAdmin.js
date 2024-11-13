import React, {useState} from 'react';
import axiosInstance from "../axiosInstance";

const QnADetailAdmin = ({ question, onClose, onUpdateQuestions  }) => {
    const [isEditing, setIsEditing] = useState(false);
    const [updatedTitle, setUpdatedTitle] = useState(question.title);
    const [updatedDescription, setUpdatedDescription] = useState(question.description);
    const [updatedStatus, setUpdatedStatus] = useState(question.status);

    const handleEditToggle = () => {
        setIsEditing(!isEditing);
    };

    const handleEdit = async (e) => {
        e.preventDefault();
        const updatedQuestion = {
            ...question,
            title: updatedTitle,
            description: updatedDescription,
            status: updatedStatus,
        };
        try {
            const response = await axiosInstance.put(`/api/v1/qna/${question.id}`, updatedQuestion);
            onUpdateQuestions(response.data); // 질문 목록 업데이트
            setIsEditing(false); // 수정 모드 종료
        } catch (error) {
            console.error("질문 수정에 실패했습니다.", error);
        }
    };

    const handleDelete = async () => {
        if (window.confirm("이 질문을 삭제하시겠습니까?")) {
            try {
                await axiosInstance.delete(`/api/v1/qna/${question.id}`);
                onUpdateQuestions(null, question.id); // 삭제된 질문 ID로 목록 업데이트
                onClose(); // 모달 닫기
            } catch (error) {
                console.error("질문 삭제에 실패했습니다.", error);
            }
        }
    };


    return (
        <div className="qna-modal">
            <h2>{question.title}</h2>
            <p>{question.description}</p>
                    <button className="qna-modal-button" onClick={handleEditToggle}>
                        {isEditing ? '취소' : '수정'}
                    </button>
                    {isEditing && (
                        <form onSubmit={handleEdit}>
                            <input
                                type="text"
                                value={updatedTitle}
                                onChange={(e) => setUpdatedTitle(e.target.value)}
                                required
                            />
                            <textarea
                                value={updatedDescription}
                                onChange={(e) => setUpdatedDescription(e.target.value)}
                                required
                            />
                            <select value={updatedStatus} onChange={(e) => setUpdatedStatus(e.target.value)}>
                                <option value="ANSWERED">답변됨</option>
                                <option value="IN_PROGRESS">문제 해결중임</option>
                                <option value="UNANSWERED">답변되지 않음</option>
                            </select>
                            <button className="qna-modal-button" type="submit">저장</button>
                        </form>
                    )}
            <button className="qna-modal-button" onClick={handleDelete}>삭제</button>
            <button className="qna-modal-button" onClick={onClose}>닫기</button>
        </div>
    );
};

export default QnADetailAdmin;
