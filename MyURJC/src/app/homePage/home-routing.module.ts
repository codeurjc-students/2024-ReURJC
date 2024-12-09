import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { HomePage } from './home.page';
import { FinalsInfoComponent } from './finals-info/finals-info.component';
import { AuthGuard } from '../services/AuthService/auth-guard.component';
import { CalendarComponent } from './components/calendar/calendar.component';
import { SubjectScheduleComponent } from './subject-schedule/subject-schedule.component';
import { PostulateAsDelegateComponent } from './postulate-as-delegate/postulate-as-delegate.component';
import { EventGuard } from '../services/EventService/event-guard.guard';
import { VoteDelegatesComponent } from './vote-delegates/vote-delegates.component';
import { voteDelegateGuardGuard } from '../services/EventService/vote-delegate-guard.guard';
import { FinalMarksComponent } from './final-marks/final-marks.component';
import { CalendarWithClickComponent } from './components/calendar/calendarWithClick/calendar-with-click/calendar-with-click.component';
import { NewsComponent } from './news/news.component';
import { AsistenciaComponent } from './bluetooth-page/asistencia.component';

const routes: Routes = [
  {
    path: '',
    component: HomePage
  },
  {
    path: 'me/subjects',
    component: FinalsInfoComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'calendar',
    component: CalendarComponent
  },
  {
    path: 'news',
    component: NewsComponent
  },

  {
    path: 'schedule',
    component: SubjectScheduleComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'becomeDelegate',
    component: PostulateAsDelegateComponent,
    canActivate: [AuthGuard, EventGuard]
  },
  {
    path: 'voteDelegate',
    component: VoteDelegatesComponent,
    canActivate: [AuthGuard, voteDelegateGuardGuard]
  },

  {
    path: 'me/finalMarks',
    component: FinalMarksComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'reservations',
    component: CalendarWithClickComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'asistencia',
    component: AsistenciaComponent,
    canActivate: [AuthGuard]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class HomePageRoutingModule { }
