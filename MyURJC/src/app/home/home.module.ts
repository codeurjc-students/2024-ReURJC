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
import { SubjectScheduleComponent } from './subject-schedule/subject-schedule.component';
import { PostulateAsDelegateComponent } from './postulate-as-delegate/postulate-as-delegate.component';
import { VoteDelegatesComponent } from './vote-delegates/vote-delegates.component';
import { FinalMarksComponent } from './final-marks/final-marks.component';
import { NotificationComponent } from './notification/notification/notification.component';
import { CalendarWithClickComponent } from './calendar/calendarWithClick/calendar-with-click/calendar-with-click.component';
import { CarnetComponent } from './carnet/carnet.component';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    HomePageRoutingModule,
  ],
  declarations: [HomePage, CardComponent, FinalsInfoComponent, CalendarComponent,  NewsComponent, SubjectScheduleComponent, PostulateAsDelegateComponent, VoteDelegatesComponent, FinalMarksComponent, NotificationComponent, CalendarWithClickComponent,CarnetComponent]
})
export class HomePageModule {}
