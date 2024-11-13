import React, { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import './ChatComponentAdmin.css';

const ChatComponentAdmin = ({ user }) => {
    const [client, setClient] = useState(null);
    const [sendMessages, setSendMessages] = useState(new Map());
    const [inputMessage, setInputMessage] = useState('');
    const [currentUser, setCurrentUser] = useState(null);

    // STOMP 클라이언트 설정 및 메시지 처리
    useEffect(() => {
        const stompClient = new Client({
            webSocketFactory: () => new SockJS('http://localhost:8090/ws'),  // 웹소켓 엔드포인트
            debug: (str) => console.log(str),
            onConnect: () => {
                console.log('Connected');
                stompClient.subscribe('/topic/admin', (message) => {
                    if (message.body) {
                        const msg = JSON.parse(message.body);
                        if (msg.sender !== 'admin') {
                            // sender를 기준으로 메시지를 Map에 저장
                            setSendMessages((prevMessages) => {
                                const newMessages = new Map(prevMessages);

                                if (!newMessages.has(msg.sender)) {
                                    newMessages.set(msg.sender, []);
                                }

                                newMessages.get(msg.sender).push(msg);
                                return newMessages;
                            });
                        }
                    }
                });
            },
            onStompError: (frame) => {
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
            },
        });

        setClient(stompClient);
        stompClient.activate(); // 웹소켓 연결 시작

        // 컴포넌트 언마운트 시 연결 종료
        return () => {
            stompClient.deactivate();
        };
    }, [user]);

    // 메시지 전송 함수
    const handleSendMessage = (e) => {
        if (client && inputMessage && currentUser) {
            e.preventDefault();

            const chatMessageAd = {
                type: 'CHAT',
                content: inputMessage,
                sender: 'admin',  // 보내는 사람은 사용자
                receiver: currentUser  // 받는 사람은 선택된 currentUser
            };

            // 메시지 전송
            client.publish({ destination: '/app/chat.sendMessage', body: JSON.stringify(chatMessageAd) });

            setInputMessage(''); // 메시지 전송 후 입력창 초기화

            setSendMessages((prevMessages) => {
                const newMessages = new Map(prevMessages);

                if (!newMessages.has(chatMessageAd.receiver)) {
                    newMessages.set(chatMessageAd.receiver, []);
                }

                const currentMessages = newMessages.get(chatMessageAd.receiver);
                const lastMessage = currentMessages[currentMessages.length - 1];
                if (!lastMessage || lastMessage.content !== chatMessageAd.content || lastMessage.sender !== chatMessageAd.sender) {
                    currentMessages.push(chatMessageAd);
                }

                return newMessages;
            });
        }
    };

    return (
        <div className="adm-chat-container">
            <h2>실시간 채팅 상담</h2>

            {/* 채팅방 목록 */}
            <div className="adm-chat-user-list">
                <h3>채팅 상대 목록</h3>
                {Array.from(sendMessages.entries()).map(([sender, messages]) => (
                    <div
                        key={sender}
                        className="adm-chat-user"
                        onClick={() => setCurrentUser(sender)} // sender 클릭 시 currentUser 설정
                    >
                        {sender}
                    </div>
                ))}
            </div>

            {/* 선택한 currentUser와의 채팅 */}
            {currentUser && (
                <div className="adm-chat-room">
                    <h3>{currentUser}와의 채팅</h3>
                    <div className="adm-chat-messages">
                        {sendMessages.get(currentUser)?.map((msg, index) => (
                            <div key={index} className="adm-chat-message">
                                <strong>{msg.sender}: </strong>{msg.content}
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* 메시지 입력 폼 */}
            <form className="adm-form" onSubmit={handleSendMessage}>
                <input
                    className="adm-chat-input"
                    type="text"
                    value={inputMessage}
                    onChange={(e) => setInputMessage(e.target.value)}
                    placeholder="메시지를 입력하세요"
                />
                <button className="adm-chat-button" type="submit">전송</button>
            </form>
        </div>
    );
};

export default ChatComponentAdmin;