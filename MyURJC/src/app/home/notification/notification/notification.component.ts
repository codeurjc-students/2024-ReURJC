import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { NotificationService } from 'src/app/services/NotificationService/notification.service';
import { NotificationInfo } from 'src/app/services/NotificationService/NotificationInfo';
import { SubjectMark } from 'src/app/services/UserService/SubjectMark';
import { WebSocketService } from 'src/app/services/webSockets/web-socket.service';
import { StatefulNotifications } from './StatefulNotifications';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss'],
})
export class NotificationComponent implements OnInit {
  notifications: StatefulNotifications[] = [];
  dbNotifications: NotificationInfo[] = [];
  @Output() notificationChange = new EventEmitter<boolean>();

  constructor(private webSocketService: WebSocketService, private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.webSocketService.connect();

    // Suscribirse a notificaciones desde el WebSocket
    this.webSocketService.notifications$.subscribe((notification) => {
      console.log('Nueva notificación:', notification);
      this.notifications.unshift(new StatefulNotifications(notification,true));
      this.notificationChange.emit(true);

      
    });

    this.notificationService.getNotifications().subscribe(data => { this.dbNotifications = data})
  }

  ngOnDestroy(): void {
    this.webSocketService.disconnect();
  }


}
