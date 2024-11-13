import React, { useState } from 'react';
import axiosInstance from "../axiosInstance";

function ProfileImageChange({ onProfileImageChange }) {
    const [file, setFile] = useState(null);

    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const formData = new FormData();
        if (file) {
            formData.append('mImage', file);
        }
        try {
            const response = await axiosInstance.put(`/api/v1/members/update-image`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            const { mimage } = response.data
            onProfileImageChange(mimage);
            alert("success")
        } catch (error) {
            console.error('프로필 이미지 업데이트 에러:', error);
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <div>
                <label>새 프로필 이미지:</label>
                <input type="file" onChange={handleFileChange} />
            </div>
            <button type="submit">업데이트</button>
        </form>
    );
}

export default ProfileImageChange;
