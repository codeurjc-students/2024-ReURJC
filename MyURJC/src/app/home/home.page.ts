import { Component, OnInit, ViewChild } from '@angular/core';
import { IonContent, IonTab, IonTabs } from '@ionic/angular';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss'],
})
export class HomePage {
onTabChange(event: any) {
  if (this.previousTab === 'alerts' && event.tab !== 'alerts') {
    // Se acaba de salir de la pestaña "alerts"
    this.showNotificationBadge = false; 
  }
  this.previousTab = event.tab; 
}
  
  showNotificationBadge = false
  previousTab: string | undefined;



  constructor() { }

  hideNotificationBadge() {
    this.showNotificationBadge = false;
  }

  onScroll(event: any) {
    this.showNotificationBadge = false; 
  }



}
