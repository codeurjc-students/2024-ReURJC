// notification.component.ts
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { NotificationService } from 'src/app/services/NotificationService/notification.service';
import { NotificationInfo } from 'src/app/services/NotificationService/NotificationInfo';
import { WebSocketService } from 'src/app/services/webSockets/web-socket.service';
import { StatefulNotifications } from './StatefulNotifications';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss'],
})
export class NotificationComponent implements OnInit {

  @Output() notificationChange = new EventEmitter<boolean>();
  dbNotifications: NotificationInfo[] = [];
  notifications: StatefulNotifications[] = [];

  constructor(private sseService: WebSocketService, private notificationService: NotificationService) { }

  ngOnInit() {
    // Carga inicial de notificaciones desde el backend
    this.notificationService.getNotifications().subscribe((data) => {
      this.dbNotifications = data;
    });

    // Conexión al WebSocket y manejo de notificaciones
    this.sseService.connect();
    this.sseService.notifications$.subscribe((notification) => {
      // Agregar la notificación al array de notificaciones
      this.notifications.unshift(new StatefulNotifications(notification, true));
      this.notificationChange.emit(true);
    });
  }


}