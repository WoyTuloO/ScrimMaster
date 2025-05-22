export interface TeamDTO {
    teamId: number;
    teamName: string;
    captainUsername: string;
    playerUsernames: string[];
    teamRanking: number;
}

export interface MatchRequest {
    team1Id: number;
    team2Id: number;
}

export interface PlayerStat {
    username: string;
    kills: number;
    deaths: number;
}

export interface SubmissionRequest {
    finalScore: number;
    opponentScore: number;
    stats: PlayerStat[];
}
