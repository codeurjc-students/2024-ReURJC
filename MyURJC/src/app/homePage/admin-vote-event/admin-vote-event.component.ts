import { Component, OnInit } from '@angular/core';
import { AdminService } from 'src/app/services/AdminService/admin.service';
import { VoteDelegateEvent } from 'src/app/services/UserService/VoteDelegateEvent';


@Component({
  selector: 'app-vote-events',
  templateUrl: './admin-vote-event.component.html',
  styleUrls: ['./admin-vote-event.component.scss'],
})
export class VoteEventsPage implements OnInit {

  voteEvents: VoteDelegateEvent[] = [];
  votesByEvent: { [key: number]: { userId: number, count: number }[] } = {};
  showDetails: { [key: number]: boolean } = {};

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.loadEvents();
  }

  loadEvents() {
    this.adminService.getAllEvents().subscribe(
      (events) => {
        this.voteEvents = events;
      },
      (error) => {
        console.error('Error fetching events', error);
      }
    );
  }

  toggleDetails(eventId: number) {
    if (this.showDetails[eventId]) {
      this.showDetails[eventId] = false;
    } else {
      this.loadVotes(eventId);
      this.showDetails[eventId] = true;
    }
  }

  loadVotes(eventId: number) {
    if (!this.votesByEvent[eventId]) {
      this.adminService.getAllVotes(eventId).subscribe(
        (votes: any) => {
          if (Array.isArray(votes)) {
            this.votesByEvent[eventId] = votes.map((vote: any) => {
              const [userId, count] = vote;
              return {
                userId: userId as number,
                count: count as number,
              };
            });
          } else {
            console.error(`Unexpected format for votes data: `, votes);
          }
        },
        (error) => {
          console.error(`Error fetching votes for event ${eventId}`, error);
        }
      );
    }
  }
  
}
