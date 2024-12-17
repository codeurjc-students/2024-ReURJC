import { Injectable } from '@angular/core';
import { fromEvent, map, Observable, Subject } from 'rxjs';
import { Client, Stomp } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { SubjectMark } from '../UserService/SubjectMark'; // Adjust path as needed

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private notificationSubject = new Subject<any>();
  notifications$ = this.notificationSubject.asObservable();




  
    connect() {
    const socket = new SockJS('http://localhost:8080/grades');
    const stompClient = Stomp.over(socket);

    stompClient.connect(
      {},
      (frame: string) => {
        console.log('Connected: ' + frame);

        // Suscripción al tópico
        stompClient.subscribe('/user/topic/private-messages', (message) => {
          const response = JSON.parse(message.body);
          console.log(response.content);

          // Emitimos la notificación
          this.notificationSubject.next(response.content);
        });
      },
      { withCredentials: true }
    );
  }

  
}