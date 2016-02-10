package barqsoft.footballscores;

import android.content.Context;

import java.util.Locale;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilies {
    public static final int SERIE_A = 357;
    public static final int PREMIER_LEGAUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;

    public static String getLeague(Context context, int league_num) {
        switch (league_num) {
            case SERIE_A:
                return context.getResources().getString(R.string.league_serie_a);
            case PREMIER_LEGAUE:
                return context.getResources().getString(R.string.league_premier_legaue);
            case CHAMPIONS_LEAGUE:
                return context.getResources().getString(R.string.league_champions_league);
            case PRIMERA_DIVISION:
                return context.getResources().getString(R.string.league_primera_division);
            case BUNDESLIGA:
                return context.getResources().getString(R.string.league_bundesliga);
            default:
                return context.getResources().getString(R.string.league_unknown);
        }
    }

    public static String getMatchDay(Context context, int match_day, int league_num) {
        if (league_num == CHAMPIONS_LEAGUE) {
            if (match_day <= 6) {
                return context.getString(R.string.matchday_group_stages);
            } else if (match_day == 7 || match_day == 8) {
                return context.getString(R.string.matchday_first_knockout);
            } else if (match_day == 9 || match_day == 10) {
                return context.getString(R.string.matchday_quarter_final);
            } else if (match_day == 11 || match_day == 12) {
                return context.getString(R.string.matchday_semi_final);
            } else {
                return context.getString(R.string.matchday_final);
            }
        } else {
            return context.getString(R.string.matchday_) + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals, int awaygoals) {
        if (home_goals < 0 || awaygoals < 0) {
            return " - ";
        } else {
//            if(isRTL(Locale.getDefault()))
//                return String.valueOf(awaygoals) + " - " + String.valueOf(home_goals);
//            else
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static boolean isRTL(Locale locale) {
        final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT
                || directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }

    public static int getTeamCrestByTeamName(Context context, String teamname) {
        if (teamname == null) {
            return R.drawable.no_icon;
        }

        //This is the set of icons that are currently in the app. Feel free to find and add more
        if(teamname.equalsIgnoreCase(context.getString(R.string.team_name_arsenal_london_fc))) {
            return R.drawable.arsenal;
        } else if(teamname.equalsIgnoreCase(context.getString(R.string.team_name_manchester_united_fc))) {
            return R.drawable.manchester_united;
        } else if(teamname.equalsIgnoreCase(context.getString(R.string.team_name_swansea_city))) {
            return R.drawable.swansea_city_afc;
        } else if(teamname.equalsIgnoreCase(context.getString(R.string.team_name_leicester_city))) {
            return R.drawable.leicester_city_fc_hd_logo;
        } else if(teamname.equalsIgnoreCase(context.getString(R.string.team_name_everton_fc))) {
            return R.drawable.everton_fc_logo1;
        } else if(teamname.equalsIgnoreCase(context.getString(R.string.team_name_west_ham_united_fc))) {
            return R.drawable.west_ham;
        } else if(teamname.equalsIgnoreCase(context.getString(R.string.team_name_tottenham_hotspur_fc))) {
            return R.drawable.tottenham_hotspur;
        } else if(teamname.equalsIgnoreCase(context.getString(R.string.team_name_west_bromwich_albion))) {
            return R.drawable.west_bromwich_albion_hd_logo;
        } else if(teamname.equalsIgnoreCase(context.getString(R.string.team_name_sunderland_afc))) {
            return R.drawable.sunderland;
        } else if(teamname.equalsIgnoreCase(context.getString(R.string.team_name_stoke_city_fc))) {
            return R.drawable.stoke_city;
        } else {
            return R.drawable.no_icon;
        }
    }
}
