import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Client } from '@stomp/stompjs'; // Importa el cliente STOMP
import * as SockJS from 'sockjs-client'; // Importa SockJS
import { SubjectMark } from '../UserService/SubjectMark';


@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private stompClient: Client | null = null;
  private notifications = new Subject<SubjectMark>();

  notifications$ = this.notifications.asObservable();

  connect(): void {
    const socket = new SockJS('http://localhost:8080/newGrade');
    this.stompClient = new Client({
      webSocketFactory: () => socket,
      debug: (str) => console.log(str),
    });

    this.stompClient.onConnect = () => {
      console.log('Conectado al WebSocket');
      this.stompClient?.subscribe('/topic/newGrade', (message) => {
        const notification: SubjectMark = JSON.parse(message.body);
        this.notifications.next(notification);
      });
    };

    this.stompClient.onStompError = (frame) => {
      console.error('Error en STOMP:', frame);
    };

    this.stompClient.activate();
  }

  disconnect(): void {
    if (this.stompClient?.connected) {
      this.stompClient.deactivate();
    }
  }
}
