import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { HomePageRoutingModule } from './home-routing.module';

import { HomePage } from './home.page';
import { CardComponent } from './card/card.component';
import { FinalsInfoComponent } from './finals-info/finals-info.component';
import { NewsComponent } from '../news/news/news.component';
import { CalendarComponent } from './calendar/calendar.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    HomePageRoutingModule,
  ],
  declarations: [HomePage, CardComponent, FinalsInfoComponent, CalendarComponent,  NewsComponent]
})
export class HomePageModule {}
