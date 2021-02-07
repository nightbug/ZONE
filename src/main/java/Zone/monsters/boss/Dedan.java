package Zone.monsters.boss;

import Zone.util.BetterSpriterAnimation;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.AngerPower;

import static Zone.Zone.makeID;
import static Zone.util.actionShortcuts.*;

public class Dedan extends CustomMonster
{
    public static final String ID = makeID(Dedan.class.getSimpleName());
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    private static final byte MINUTE_HAND = 1;
    private static final byte HOUR_HAND = 2;
    private static final byte SWEEP_HAND = 3;

    private static final int HP = 550;
    private static final int A9_HP = 600;
    private static final int ENRAGE = 1;
    private static final int ENRAGE_A9 = 2;
    private static final int MINUTE_HAND_DAMAGE = 8;
    private static final int HOUR_HAND_DAMAGE = 18;
    private static final int SWEEP_HAND_DAMAGE = 8;
    private static final int SWEEP_HAND_HITS = 3;

    private int minuteHand;
    private int hourHand;
    private int sweepHand;
    private int sweepHandHits;
    private int enrage;

    private boolean usedMinute = false;
    private boolean usedHour = false;
    public Dedan() {
        this(0.0f, 0.0f);
    }

    public Dedan(final float x, final float y) {
        super(Dedan.NAME, ID, HP, -5.0F, 0, 280.0f, 340.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("zoneResources/images/monsters/boss/dedan/Spriter/dedanAnimation.scml");
        this.type = EnemyType.BOSS;
        this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;
        setHp(AbstractDungeon.ascensionLevel >= 9 ? A9_HP : HP);
        enrage = AbstractDungeon.ascensionLevel >= 9 ? ENRAGE_A9: ENRAGE;
        minuteHand = MINUTE_HAND_DAMAGE;
        hourHand = HOUR_HAND_DAMAGE;
        sweepHand = SWEEP_HAND_DAMAGE;
        sweepHandHits = SWEEP_HAND_HITS;

        this.damage.add(new DamageInfo(this, minuteHand));
        this.damage.add(new DamageInfo(this, hourHand));
        this.damage.add(new DamageInfo(this, sweepHand));

        Player.PlayerListener listener = new DedanListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    public void usePreBattleAction() {
        // play bgm here.
        doPow(this, new AngerPower(this, enrage));
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case MINUTE_HAND: {
                doDmg(p(), damage.get(0).output);
                break;
            }
            case HOUR_HAND: {
                doDmg(p(), damage.get(1).output);
                break;
            }
            case SWEEP_HAND: {
                for(int i = 0; i < sweepHandHits; i += 1){ doDmg(p(), damage.get(2).output); }
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (!usedMinute) {
            this.setMove(Dedan.MOVES[0], MINUTE_HAND, Intent.ATTACK, damage.get(0).base);
            usedMinute = true;
        }  else if (!usedHour) {
            this.setMove(Dedan.MOVES[1], HOUR_HAND, Intent.ATTACK, damage.get(1).base);
            usedHour = true;
        } else {
            this.setMove(Dedan.MOVES[2], SWEEP_HAND, Intent.ATTACK, damage.get(2).base, SWEEP_HAND_HITS, true);
            usedMinute = false;
            usedHour = false;
        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
        NAME = Dedan.monsterStrings.NAME;
        MOVES = Dedan.monsterStrings.MOVES;
        DIALOG = Dedan.monsterStrings.DIALOG;
    }

    @Override
    public void die(boolean triggerRelics) {
        ((BetterSpriterAnimation)this.animation).startDying();
        this.onBossVictoryLogic();
        super.die(triggerRelics);
    }

    //Runs a specific animation
    public void runAnim(String animation) { ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(animation); }

    //Resets character back to idle animation
    public void resetAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation("idle");
    }

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class DedanListener implements Player.PlayerListener {

        private Dedan character;

        public DedanListener(Dedan character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if (!animation.name.equals("idle")) { character.resetAnimation(); }
        }

        //UNUSED
        public void animationChanged(Animation var1, Animation var2){

        }

        //UNUSED
        public void preProcess(Player var1){

        }

        //UNUSED
        public void postProcess(Player var1){

        }

        //UNUSED
        public void mainlineKeyChanged(com.brashmonkey.spriter.Mainline.Key var1, com.brashmonkey.spriter.Mainline.Key var2){

        }
    }
}