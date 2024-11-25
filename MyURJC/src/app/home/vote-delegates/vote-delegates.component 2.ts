import { Component, OnInit } from '@angular/core';
import { EventServiceService } from 'src/app/services/EventService/event-service.service';
import { ApiUserService } from 'src/app/services/UserService/api.user.service';
import { User } from 'src/app/services/UserService/user.model';

@Component({
  selector: 'app-vote-delegates',
  templateUrl: './vote-delegates.component.html',
  styleUrls: ['./vote-delegates.component.scss'],
})
export class VoteDelegatesComponent  implements OnInit {

   candidates: User[] = []
   private userVoted: boolean = false

  constructor(private userService: ApiUserService, private eventService: EventServiceService) { }

  ngOnInit() {
    this.userService.getCandidates().subscribe(response => {
      this.candidates = response;
    })

    this.userService.hasVoted().subscribe(data => 
      { this.userVoted = data}
      )
  }

  voteForCandidate(candidate: number) {
    this.userService.vote(candidate)
    this.userService.hasVoted().subscribe(data => 
    { this.userVoted = data}
    )
  }

  getUserVoted():boolean {
    return this.userVoted;
  }
  

}
