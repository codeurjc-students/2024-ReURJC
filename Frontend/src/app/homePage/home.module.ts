import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { HomePageRoutingModule } from './home-routing.module';

import { HomePage } from './home.page';
import { FinalsInfoComponent } from './finals-info/finals-info.component';
import { CalendarComponent } from './components/calendar/calendar.component';
import { SubjectScheduleComponent } from './subject-schedule/subject-schedule.component';
import { PostulateAsDelegateComponent } from './postulate-as-delegate/postulate-as-delegate.component';
import { VoteDelegatesComponent } from './vote-delegates/vote-delegates.component';
import { FinalMarksComponent } from './final-marks/final-marks.component';
import { NotificationComponent } from './components/notification/notification/notification.component';
import { CalendarWithClickComponent } from './components/calendar/calendarWithClick/calendar-with-click/calendar-with-click.component';
import { CarnetComponent } from './components/carnet/carnet.component';
import { CardComponent } from './components/Card/card/card.component';
import { NewsComponent } from './news/news.component';
import { AsistenciaComponent } from './bluetooth-page/asistencia.component';
import { VoteEventsPage } from './admin-vote-event/admin-vote-event.component';
import { AdminSportReservationComponent } from './admin-sport-reservation/admin-sport-reservation.component';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    HomePageRoutingModule,

  ],
  declarations: [HomePage, CardComponent, FinalsInfoComponent, CalendarComponent, NewsComponent, SubjectScheduleComponent, PostulateAsDelegateComponent, VoteDelegatesComponent, FinalMarksComponent, NotificationComponent, CalendarWithClickComponent, CarnetComponent, AsistenciaComponent, VoteEventsPage, AdminSportReservationComponent ]
})
export class HomePageModule { }
