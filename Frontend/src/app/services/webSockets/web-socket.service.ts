import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Stomp } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

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

        // Suscripción al tópico
        stompClient.subscribe('/user/topic/private-messages', (message) => {
          const response = JSON.parse(message.body);

          // Emitimos la notificación
          this.notificationSubject.next(response.content);
        });
      },
      { withCredentials: true }
    );
  }

  
}