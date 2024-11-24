import { Component, OnInit, ViewChild } from '@angular/core';
import { IonContent } from '@ionic/angular';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss'],
})
export class HomePage{
  
  showNotificationBadge = false



  constructor() { }

  hideNotificationBadge() {
    this.showNotificationBadge = false;
  }

  onScroll(event: any) {
    this.showNotificationBadge = false; 
  }



}
